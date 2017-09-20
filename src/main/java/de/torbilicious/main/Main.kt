package de.torbilicious.main

import com.google.gson.Gson
import khttp.get
import java.util.regex.Pattern


val baseUrl = "https://www.foaas.com"

fun main(args: Array<String>) {
    val operationsString = get("$baseUrl/operations").text

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
    val insultHtml = get("$baseUrl${insertValues(operation.url)}").text
    val insult = extractInsultFromHtml(insultHtml)

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

fun extractInsultFromHtml(html: String): String {
    val pattern = Pattern.compile("<title>FOAAS - (.*?)</title>")
    val matcher = pattern.matcher(html)
    val matches: MutableList<String> = mutableListOf()

    while (matcher.find()) {
        matches.add(matcher.group(1))
    }

    return if (matches.size == 0) {
        throw NoMatchFoundException()
    } else {
        matches[0]
    }
}

class NoMatchFoundException : Exception()

data class Operation(val name: String, val url: String, val fields: Array<Field>)
data class Field(val name: String, val field: String)
