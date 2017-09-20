package de.torbilicious.main

import com.google.gson.Gson
import khttp.get


val baseUrl = "https://www.foaas.com"
val headers = mapOf("Accept" to "text/plain")
val validInserts = mapOf("from" to "Me",
        "company" to "SomeCompany",
        "name" to "You")

fun main(args: Array<String>) {
    val operationsString = get("$baseUrl/operations", headers).text

    val gson = Gson()
    val operations = gson.fromJson(operationsString, Array<Operation>::class.java)

    val supportedOperations = operations.filter {
        it.fields.all {
            validInserts.containsKey(it.field)
        }
    }

    val operation = getRandomOperation(supportedOperations)
    val insult = get("$baseUrl${insertValues(operation.url)}", headers).text

    println("Insult: \n$insult")

    sendMail(insult)
}

fun getRandomOperation(operations: List<Operation>): Operation {
    return operations[(Math.random() * operations.size).toInt()]
}

fun insertValues(url: String): String {

    var urlWithValues = url

    for((key, value) in validInserts) {
        urlWithValues = urlWithValues.replace(":$key", value)
    }

    return urlWithValues
}

data class Operation(val name: String, val url: String, val fields: Array<Field>)
data class Field(val name: String, val field: String)
