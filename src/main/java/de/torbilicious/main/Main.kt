package de.torbilicious.main

import com.google.gson.Gson
import khttp.get


val baseUrl = "https://www.foaas.com"
val headers = mapOf("Accept" to "text/plain")

fun main(args: Array<String>) {
    val operationsString = get("$baseUrl/operations", headers).text

    val gson = Gson()
    val operations = gson.fromJson(operationsString, Array<Operation>::class.java)

    val sanitizedOperations = operations.filter {
        it.fields.all {
            it.field == "from"
            || it.field == "company"
            || it.field == "name"
        }
    }

    val operation = getRandomOperation(sanitizedOperations)
    val insult = get("$baseUrl${insertValues(operation.url)}", headers).text

    println("Insult: \n$insult")

    sendMail(insult)
}

fun getRandomOperation(operations: List<Operation>): Operation {
    return operations[(Math.random() * operations.size).toInt()]
}

fun insertValues(url: String): String {
    val company = "SomeCompany"
    val from = "Me"
    val name = "You"

    return url.replace(":company", company)
            .replace(":from", from)
            .replace(":name", name)
}

data class Operation(val name: String, val url: String, val fields: Array<Field>)
data class Field(val name: String, val field: String)
