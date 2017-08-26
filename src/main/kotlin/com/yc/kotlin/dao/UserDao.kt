package com.yc.kotlin.dao

import com.yc.kotlin.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Administrator on 2017/8/25.
 */
@Transactional
interface UserDao : CrudRepository<User, Long> {
    fun findByEmail(email:String):User?
}