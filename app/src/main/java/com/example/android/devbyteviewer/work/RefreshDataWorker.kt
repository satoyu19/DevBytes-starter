package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.repository.VideosRepository
import retrofit2.HttpException
import timber.log.Timber

//NOTE:
// Worker このクラスはバックグラウンドで動作させたい実際の作業（タスク）を定義する場所です。このクラスを継承し、doWork()メソッドをオーバーライドします。
// doWork()メソッドはサーバーとのデータの同期や画像処理など、バックグラウンドで行わせたい処理のコードを書く場所です。このタスクでWorkerを実装していきます。
class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object{
//        タスク名
        const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }

    //バックグラウンドスレッドで呼び出される
    override suspend fun doWork(): Result {     //Result　→　 success(), failure(), or retry()
        val database = getDatabase(applicationContext)
        val repository = VideosRepository(database)

        try{
            repository.refreshVideos()
            Timber.d("Work request for sync is run")
        } catch (e: HttpException){
            return Result.retry()     //タスクが一時的に失敗し、リトライされるべき場合に呼び出す
        }

        return Result.success()     //タスクが正常に完了した
    }

}