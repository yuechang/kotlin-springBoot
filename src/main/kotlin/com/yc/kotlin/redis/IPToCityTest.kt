package com.yc.kotlin.redis

import com.google.gson.Gson
import com.yc.kotlin.entity.City
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.File
import java.io.FileReader


var GEOLITECITY_LOCATION = "C:\\Users\\Administrator\\Desktop\\GeoLiteCity_20170801\\GeoLiteCity-Location.csv"
var GEOLITECITY_BLOCKS = "C:\\Users\\Administrator\\Desktop\\GeoLiteCity_20170801\\GeoLiteCity-Blocks.csv"
var IP2CITYID = "ip2cityid:"
var CITYID2CITY = "cityid2city:"

fun main(args: Array<String>) {
    /*
    var ipAddress = "58.60.230.138"
    val score = ipToScore(ipAddress)
    print(score)
*/
    /*
    var csvFilePath = "C:\\Users\\Administrator\\Desktop\\GeoLiteCity_20170801\\test.csv"
    readerCsv(csvFilePath)
    */

    // ip导入redis中
    importIpToRedis(getJedis(),File(GEOLITECITY_BLOCKS))
    // 将cityId与对应的城市信息导入redis中
    citiesToRedis(getJedis(),File(GEOLITECITY_LOCATION))

    // Tokyo
    var ipAddress = "52.196.210.28"
    // Shenzhen
    ipAddress = "58.60.230.138"
    val city = findByIp(jedis = getJedis(), ip = ipAddress)
    println(city)

}

fun getJedis(): Jedis {

    // 池基本配置
    val config = JedisPoolConfig()
    config.maxTotal = 100
    config.maxIdle = 5
    config.maxWaitMillis = 100000
    config.testOnBorrow = false

    val jedisPool = JedisPool(config, "192.168.10.242", 6381, 10000, "admin")
    return jedisPool.resource
}


fun ipToScore(ipAddress:String):Long{
    var score = 0L
    for(v in ipAddress.split(".")){
        score = score * 256 + v.toLong()
    }
    return score
}


fun importIpToRedis(jedis:Jedis,file:File){

    val pipeline = jedis.pipelined()

    println("导入ip地址开始")
    val startTime = System.currentTimeMillis()
    val reader = FileReader(file)
    val parser = CSVParser(reader, CSVFormat.newFormat(','))

    val records = parser.records

    for (i in records.indices){

        val recordNumber = records[i].recordNumber
        if (recordNumber < 3)
            continue
        val start = replaceSprit(records[i].get(0)).toDouble()
        val id = records[i].get(2)
        pipeline.zadd(IP2CITYID,start,replaceSprit(id + "_" + i))
        println("start:" + start.toString()  + ",id:" + id)
    }
    pipeline.syncAndReturnAll()
    pipeline.close()
    val endTime = System.currentTimeMillis();
    println("------------------")
    println("导入IP地址完成")
    println("耗时：" + (endTime - startTime)/1000.0)
}

fun citiesToRedis(jedis: Jedis,file: File){

    val gson = Gson ()
    val pipeline = jedis.pipelined()
    try {

        println("导入城市地址开始")
        val startTime = System.currentTimeMillis()
        val reader = FileReader(file)
        val parser = CSVParser(reader, CSVFormat.newFormat(','))

        val list = parser.records
        for (i in list.indices){
            // 本行元素数
            val num = list[i].recordNumber
            if(num < 3)
                continue
            val city = record2City(record = list[i])
            val json = gson.toJson(city)
            println("cityId:" + city.cityId + ",info:" + json)
            pipeline.hset(CITYID2CITY,city.cityId,json)
        }
        pipeline.syncAndReturnAll()
        pipeline.close()
        val endTime = System.currentTimeMillis();
        println("------------------")
        println("导入IP地址完成")
        println("耗时：" + (endTime - startTime)/1000.0)
    } catch (e: Exception){
        e.printStackTrace()
    }
}

fun record2City(record: CSVRecord):City {
    val city:City = City()
    city.cityId = replaceSprit(record.get(0))
    city.country = replaceSprit(record.get(1))
    city.region = replaceSprit(record.get(2))
    city.city = replaceSprit(record.get(3))
    city.postalCode = replaceSprit(record.get(4))
    city.latitude = replaceSprit(record.get(5))
    city.longitude = replaceSprit(record.get(6))
    city.metroCode = replaceSprit(record.get(7))
    city.areaCode = replaceSprit(record.get(8))
    return city
}

fun replaceSprit(str: String): String {
    return str.replace("\"".toRegex(), "")
}

fun findByIp(jedis: Jedis,ip: String):City?{

    var score = ipToScore(ip)
    println(score)
    val results = jedis.zrevrangeByScore(IP2CITYID,score.toDouble(),0.0,0,1)
    if (0 == results.size)
        return null
    var cityId = results.iterator().next()
    cityId = cityId.substring(0, cityId.indexOf('_'))
    val city = Gson().fromJson<City>(jedis.hget(CITYID2CITY, cityId),City::class.java)
    return city
}
