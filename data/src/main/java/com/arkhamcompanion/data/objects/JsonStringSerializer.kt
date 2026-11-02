package com.arkhamcompanion.data.objects

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

object JsonStringSerializer : KSerializer<JsonElement> {

    private val json = Json

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("JsonString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): JsonElement {
        val value = decoder.decodeString()
        return json.parseToJsonElement(value)
    }

    override fun serialize(encoder: Encoder, value: JsonElement) {
        encoder.encodeString(json.encodeToString(JsonElement.serializer(), value))
    }
}

object FlexibleIdSerializer : KSerializer<String> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("JSON only")

        return jsonDecoder.decodeJsonElement()
            .jsonPrimitive
            .content
    }

    override fun serialize(encoder: Encoder, value: String) {
        val number = value.toLongOrNull()

        if (number != null) {
            encoder.encodeLong(number)
        } else {
            encoder.encodeString(value)
        }
    }
}