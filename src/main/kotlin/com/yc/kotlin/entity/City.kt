package com.yc.kotlin.entity

import org.apache.commons.csv.CSVRecord

class City {
    var cityId: String? = null          //城市编码
    var country: String? = null         //国家
    var region: String? = null          //区域
    var city: String? = null            //城市名
    var postalCode: String? = null      //邮政编码
    var latitude: String? = null        //纬度
    var longitude: String? = null       //经度
    var metroCode: String? = null       //都市号
    var areaCode: String? = null        //区号

    override fun toString(): String {
        return "City(cityId=$cityId, country=$country, region=$region, city=$city, postalCode=$postalCode, latitude=$latitude, longitude=$longitude, metroCode=$metroCode, areaCode=$areaCode)"
    }
}