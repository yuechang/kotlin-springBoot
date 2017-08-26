package com.yc.kotlin.controller

import com.yc.kotlin.dao.UserDao
import com.yc.kotlin.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Administrator on 2017/8/25.
 */
@RestController
class UserController {

    @Autowired
    private var userDao:UserDao?=null


    @RequestMapping("/create")
    @ResponseBody
    public fun create(name:String,email:String): User?{

        try {
            var user = User(name = name,email = email)
            userDao?.save(user)
            return user
        } catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public fun delete(id:Long):String {

        try {
            var user = User(id)
            userDao?.delete(user)
            return id.toString() + "delete"
        }catch (e:Exception){
            e.printStackTrace()
            return "delete error " + e.message.toString()
        }
    }

    @RequestMapping("/get-by-email")
    @ResponseBody
    public fun getByEmail(email:String):User? {

        try {
            var user = userDao?.findByEmail(email)
            if (user != null){
                return user;
            } else{
                return null;
            }
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public fun update(id:Long,name:String,email:String):User? {

        try {
            var user:User? = userDao?.findOne(id)?:return null
            user?.name = name
            user?.email = email
            userDao?.save(user)
            return user;
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }
}