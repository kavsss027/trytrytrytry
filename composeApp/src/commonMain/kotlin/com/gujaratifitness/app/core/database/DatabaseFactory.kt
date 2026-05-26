package com.gujaratifitness.app.core.database

import com.gujaratifitness.app.database.AppDatabase

class DatabaseFactory(private val driverFactory: DriverFactory) {
    fun createDatabase(): AppDatabase {
        val driver = driverFactory.createDriver()
        return AppDatabase(driver)
    }
}
