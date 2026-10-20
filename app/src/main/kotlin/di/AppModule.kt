package org.deesoft.di

import org.deesoft.repository.TaskRepository
import org.deesoft.service.TaskService
import org.koin.dsl.module

val appModule = module {
    // single = สร้างครั้งเดียวใช้ร่วมกันตลอดชีพ (Singleton)
    single { TaskRepository() }

    // Koin จะฉีด TaskRepository เข้าไปใน TaskService ให้เองอัตโนมัติผ่าน get()
    single { TaskService(get()) }
}