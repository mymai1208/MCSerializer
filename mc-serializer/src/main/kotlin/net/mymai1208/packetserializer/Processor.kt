package net.mymai1208.packetserializer

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import net.mymai1208.packetserializer.annotation.Serializer
import net.mymai1208.packetserializer.annotation.VariableValue
import org.jetbrains.annotations.Nullable

@OptIn(KspExperimental::class)
class Processor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val packetClass = ClassName("net.mymai1208.packetserializer", "Packet")
    private val bufferClass = ClassName("net.minecraft.network", "PacketByteBuf")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Serializer::class.qualifiedName!!)
        val classes = symbols
            .filter { it.validate() }
            .mapNotNull { it as? KSClassDeclaration }
            .filter { it.classKind == ClassKind.CLASS }

        for (ksClassDeclaration in classes) {
            if (ksClassDeclaration.primaryConstructor == null) {
                environment.logger.error("Class ${ksClassDeclaration.qualifiedName} does not have a primary constructor")
            }

            val properties = ksClassDeclaration.getAllProperties()

            val type = generateSerializeClass(ksClassDeclaration.simpleName.asString(), properties.toList())

            FileSpec.builder("generated.packet", ksClassDeclaration.simpleName.asString())
                .addType(type)
                .build()
                .writeTo(environment.codeGenerator, Dependencies.ALL_FILES)
        }

        return emptyList()
    }

    private fun generateSerializeClass(name: String, properties: List<KSPropertyDeclaration>): TypeSpec {
        val readFunSpec = FunSpec.builder("readPacket").apply {
            addModifiers(KModifier.OVERRIDE)
            addParameter("buf", bufferClass)
            addCode(buildCodeBlock {
                for (property in properties) {
                    val isVariable = property.isAnnotationPresent(VariableValue::class)
                    val isUnlimitedNBT = property.isAnnotationPresent(UnlimitedNBT::class)

                    createReadPacket(property, isVariable, isUnlimitedNBT)
                }
            })
        }

        val writeFuncSpec = FunSpec.builder("writePacket").apply {
            addModifiers(KModifier.OVERRIDE)
            addParameter("buf", bufferClass)
            addCode(buildCodeBlock {
                for (property in properties) {
                    val isVariable = property.isAnnotationPresent(VariableValue::class)

                    createWritePacket(property, isVariable)
                }
            })
        }

        return TypeSpec.classBuilder(name).apply {
            addSuperinterface(packetClass)
            addModifiers(KModifier.DATA)

            primaryConstructor(FunSpec.constructorBuilder().apply {
                for (property in properties) {
                    val propertyTypeName = if(property.isAnnotationPresent(Nullable::class) || property.type.resolve().isMarkedNullable) property.type.toTypeName().copy(true) else property.type.toTypeName()

                    addParameter(property.simpleName.asString(), propertyTypeName)
                    addProperty(
                        PropertySpec.builder(property.simpleName.asString(), propertyTypeName)
                            .mutable(true)
                            .initializer(property.simpleName.asString())
                            .build())
                }
            }.build())

            addFunction(readFunSpec.build())
            addFunction(writeFuncSpec.build())
        }.build()
    }

    private fun CodeBlock.Builder.createWritePacket(property: KSPropertyDeclaration, isVar: Boolean = false) {
        val type = getTypeAsString(property.type.resolve())

        if(isVar) {
            addStatement("buf.writeVar${type}(${property.simpleName.asString()})")

            return
        }

        if(type == "Map") {
            val mapArgs = property.type.resolve().arguments

            val firstArg = mapArgs[0].type?.resolve()?.let { getTypeAsString(it) }
            val secondArg = mapArgs[1].type?.resolve()?.let { getTypeAsString(it) }

            addStatement("buf.writeMap(${property.simpleName.asString()}, PacketByteBuf::write${firstArg}, PacketByteBuf::write${secondArg})")

            return
        }

        addStatement("buf.write${type}(${property.simpleName.asString()})")
    }

    private fun CodeBlock.Builder.createReadPacket(property: KSPropertyDeclaration, isVar: Boolean = false, isUnlimitedNBT: Boolean = false) {
        val type = getTypeAsString(property.type.resolve())

        if(isVar) {
            addStatement("${property.simpleName.asString()} = buf.readVar${type}()")

            return
        }

        if(isUnlimitedNBT) {
            addStatement("${property.simpleName.asString()} = buf.readNbt(NbtTagSizeTracker(Long.MAX_VALUE))")

            return
        }

        if(type == "Map") {
            val mapArgs = property.type.resolve().arguments

            val firstArg = mapArgs[0].type?.resolve()?.let { getTypeAsString(it) }
            val secondArg = mapArgs[1].type?.resolve()?.let { getTypeAsString(it) }

            addStatement("${property.simpleName.asString()} = buf.readMap(PacketByteBuf::read${firstArg}, PacketByteBuf::read${secondArg})")

            return
        }

        addStatement("${property.simpleName.asString()} = buf.read${type}()!!")
    }

    //declaration.simpleName
    private fun getTypeAsString(type: KSType): String? {
        return when (type.declaration.qualifiedName?.asString()) {
            "kotlin.Int" -> "Int"
            "kotlin.String" -> "String"
            "kotlin.Boolean" -> "Boolean"
            "kotlin.Byte" -> "Byte"
            "kotlin.Short" -> "Short"
            "kotlin.Long" -> "Long"
            "kotlin.Float" -> "Float"
            "kotlin.Double" -> "Double"
            "kotlin.IntArray" -> "IntArray"
            "kotlin.LongArray" -> "LongArray"
            "kotlin.ByteArray" -> "ByteArray"
            "net.minecraft.nbt.NbtCompound" -> "Nbt"
            "net.minecraft.item.ItemStack" -> "ItemStack"
            "net.minecraft.util.Identifier" -> "Identifier"
            "net.minecraft.util.math.BlockPos" -> "BlockPos"
            "java.util.UUID" -> "Uuid"
            "net.minecraft.text.Text" -> "Text"
            "java.util.Map" -> "Map"
            "kotlin.collections.Map" -> "Map"
            else -> null
        }
    }
}