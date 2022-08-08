/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.android.devbyteviewer.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
//NOTE:
// WorkRequest このクラスはバックグラウンドでworkerを動作させるためのリクエストを表すクラスです。
// WorkRequestを使って、workerのタスクをいつ、どのように実行させるかを設定します。後のタスクでWorkRequestを実装します。
class DevByteApplication : Application() {

    //コルーチンオブジェクト
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
//        Timber.plant(Timber.DebugTree())
        delayedInit()
    }

    private fun delayedInit(){
        applicationScope.launch {
            setupRecurringWork()
            Timber.plant(Timber.DebugTree())
        }
    }

    //バックグラウンドで繰り返すタスク(PeriodicWorkRequest)のセットアップを行う,一度限りのタスク(OneTimeWorkRequest)もあり。
    private fun setupRecurringWork(){
        //Constraint　→　WorkRequest を実行する前に満たす必要のある要件(制約)を指定します。(以下は従量制ネットワークを利用しているときのみ実行される制約),その他の制約もあり
        val constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.UNMETERED)
            setRequiresBatteryNotLow(true)
            setRequiresCharging(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //この制約はユーザーが端末を積極的に使っていないときにのみリクエストを実行するようにします。これはAndroid 6.0以上でのみ使えるので、SDKバージョンをM以上にします
                setRequiresDeviceIdle(true)
            }
        }.build()

        //一日一回定期的に行わせるタスクのリクエストを作成,constraintをリクエストに設定
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS).setConstraints(constraints).build()
        //こっちであれば15分ごとに実行されるタスクとなる
//        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES).build()

        //workのスケジューリング,一意の名前を持つ PeriodicWorkRequest(定期実行タスク) を待ち行列に入れる
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
