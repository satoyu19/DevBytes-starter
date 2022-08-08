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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.network.DevByteNetwork
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

//オフラインキャッシュを管理するためのレポジトリ、ネットワークリザルトを読み取り、データベースを最新の状態にキープするためのロジックを持つ
class VideosRepository(private val database: VideosDatabase){

    //DatabaseVideo型をasDomainModel()メソッドを利用してDevByteVideo型に変換
    val videos: LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()){
        it.asDomainModel()
    }

    //オフラインキャッシュをリフレッシュするために使われるAPI、データベース操作を行いため、コルーチンで呼び出せるようにsuspend関数にする
    suspend fun refreshVideos(){
        withContext(Dispatchers.IO){
            Timber.d("refresh videos is called")
            val playlist = DevByteNetwork.devbytes.getPlaylist()
            //asDomainModelでデータベースオブジェクトにマッピングする。
            database.videoDao.insertAll(playlist.asDatabaseModel())
        }
    }
}
