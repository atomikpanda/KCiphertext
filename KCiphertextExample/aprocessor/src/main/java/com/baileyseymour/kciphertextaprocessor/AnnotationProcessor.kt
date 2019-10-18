package com.baileyseymour.kciphertextaprocessor

import com.baileyseymour.kciphertextannotation.Ciphertext
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.crypto.Cipher
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

private data class CiphertextFile(
    val fileBuilder: FileSpec.Builder,
    var ciphertextFunSpec: FunSpec.Builder?,
    var decryptFunSpec: FunSpec.Builder?
)

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private var fileMap: MutableMap<String, CiphertextFile> = mutableMapOf()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Ciphertext::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        fileMap = mutableMapOf()
        roundEnv.getElementsAnnotatedWith(Ciphertext::class.java)
            .forEach {
                if (it.kind != ElementKind.FIELD) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only classes fields can be annotated"
                    )
                    return true
                }
                processAnnotation(it)
            }

        finalize()
        return false
    }

    private fun finalize() {
        val kaptKotlinGeneratedDir: String =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
        fileMap.forEach { entry ->
            val file = entry.value
            file.ciphertextFunSpec?.let { file.fileBuilder.addFunction(it.build()) }
            file.decryptFunSpec?.let { file.fileBuilder.addFunction(it.build()) }
            file.fileBuilder.build().writeTo(File(kaptKotlinGeneratedDir))
        }
    }

    private fun getCiphertextFile(packageName: String, fileName: String): CiphertextFile {
        val key = packageName + fileName
        var file: CiphertextFile? = null
        if (!fileMap.containsKey(key)) {
            fileMap[key] = CiphertextFile(FileSpec.builder(packageName, fileName), null, null)
        }
        fileMap[key]?.let {
            file = it
        }
        return file!!
    }

    private fun processAnnotation(element: Element) {
        val fieldName = element.simpleName.toString()
        val className = element.enclosingElement.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        val fileName = className + "_Ciphertext"
        val classReceiver = ClassName.bestGuess(element.enclosingElement.toString()).copy()

        val file = getCiphertextFile(pack, fileName)

        if (file.ciphertextFunSpec == null) {
            file.ciphertextFunSpec = FunSpec.builder("ciphertext").apply {
                addModifiers(KModifier.INLINE, KModifier.PUBLIC)
                addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("\"NOTHING_TO_INLINE\"").build())
            }
        }

        if (file.decryptFunSpec == null) {
            file.decryptFunSpec = FunSpec.builder("decrypt").apply {
                addModifiers(KModifier.INLINE, KModifier.PUBLIC)
                addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("\"NOTHING_TO_INLINE\"").build())
                receiver(classReceiver)
                addParameter("byteArray", ByteArray::class)
                returns(ByteArray::class)
                addStatement("val byteList = byteArray.asList()")
                addStatement("val adjusted = byteList.run {\n" +
                        "        this.map { (it - 1).toByte() }\n" +
                        "    }")
                addStatement("return adjusted.toByteArray()")
            }
        }

        file.ciphertextFunSpec?.run {
            receiver(classReceiver)

            var varName = "this.$fieldName"
            if (element.modifiers.contains(Modifier.STATIC)) {
                varName = "$className.$fieldName"
            }

            val encryptedValue =
                processValueToByteArraySource(element.getAnnotation(Ciphertext::class.java).value)
            addStatement(
                "$varName = String(decrypt(byteArrayOf(%L)))",
                encryptedValue
            )
        }


    }

    private fun processValueToByteArraySource(value: String): String {
        return value.toByteArray().asList().map {
            (it + 1).toByte()
        }.joinToString(",")
    }
}