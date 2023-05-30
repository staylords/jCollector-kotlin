/*
 * Copyright (c) 2023 Joseph (me@staylords.com)
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: staylords
 */

package com.staylords.jcollector.services

import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.staylords.jcollector.JCollector
import com.staylords.jcollector.JCollectorConst
import com.staylords.jcollector.`object`.Collector
import com.staylords.jcollector.`object`.CollectorItem
import org.bson.Document
import org.bukkit.Material
import java.io.Closeable
import java.util.function.Consumer

/**
 * @project jCollector-kotlin
 *
 * @date 30/05/2023
 * @author me@staylords.com
 */
class MongoService(plugin: JCollector) : Closeable {

    private val client: MongoClient

    private lateinit var database: MongoDatabase

    private lateinit var collectorsCollection: MongoCollection<Document>
    private lateinit var itemsCollection: MongoCollection<Document>

    init {
        plugin.logger.info("Connecting to MongoDB...")

        client = MongoClient(MongoClientURI(JCollectorConst.MONGO_URI))

        plugin.logger.info("Connected to MongoDB!")

        try {
            database = client.getDatabase(JCollectorConst.MONGO_DATABASE)
            collectorsCollection = database.getCollection("collectors")
            itemsCollection = database.getCollection("items")
        } catch (e: Exception) {
            plugin.logger.severe("Failed to initialize backend.")
            e.printStackTrace()
        }

        collectorsCollection.createIndex(BasicDBObject("id", 1))
        itemsCollection.createIndex(BasicDBObject("displayName", 1))
    }

    fun getCollectorItemsType(callback: Consumer<Set<String>>) {
        val collectorItems = HashSet<String>()
        itemsCollection.find().forEach { collectorItems.add(it["type"].toString()) }
        callback.accept(collectorItems)
    }

    fun loadCollectorItem(type: String) {
        val document: Document = itemsCollection.find(Filters.eq("type", type)).first() ?: return

        val collectorService: CollectorService = JCollector.instance.collectorService

        val collectorItem =
            CollectorItem(document["display_name"].toString(), Material.matchMaterial(document["type"].toString()))

        collectorService.addCollectorItem(collectorItem)
    }

    fun saveCollectorItem(item: CollectorItem) {
        val document = Document()

        document.append("type", item.type.name)
        document.append("display_name", item.displayName)

        itemsCollection.replaceOne(Filters.eq("type", item.type.name), document, ReplaceOptions().upsert(true))
    }

    fun getCollectorsIds(callback: Consumer<Set<String>>) {
        val collectors = HashSet<String>()
        collectorsCollection.find().forEach { collectors.add(it["_id"].toString()) }
        callback.accept(collectors)
    }

    fun loadCollector(id: String) {
        val document: Document = collectorsCollection.find(Filters.eq("_id", id)).first() ?: return

        val collectorService: CollectorService = JCollector.instance.collectorService

        val collector = Collector(document["_id"].toString())

        // collector.storedItems
        collector.soldItemsCount = document["sold_items"] as Int
        collector.totalSalesAmount = document["total_sales"] as Double

        val storedItemsDocument = document["stored_items"] as Document

        storedItemsDocument.forEach {
            val collectorItem: CollectorItem =
                collectorService.getCollectorItem(Material.matchMaterial(it.key)) ?: return

            collector.storedItems[collectorItem] = it.value as Int
        }

        collectorService.addCollector(collector)

        println(collectorService.collectors.toString())

    }

    fun saveCollector(collector: Collector) {
        val document = Document()

        document.append("_id", collector.id)
        document.append("sold_items", collector.soldItemsCount)
        document.append("total_sales", collector.totalSalesAmount)

        val storedItemsDocument = Document()

        collector.storedItems.forEach {
            storedItemsDocument.append(it.key.type.name, it.value)
        }

        document.append("stored_items", storedItemsDocument)

        collectorsCollection.replaceOne(Filters.eq("_id", collector.id), document, ReplaceOptions().upsert(true))
    }

    override fun close() {
        client.close()
    }

}