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

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.devbyteviewer.domain.DevByteVideo

@Dao
interface VideoDao{
    //データベースから動画を読み取るメソッド
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>  //LiveDate型を返すことでUIに表示されるデータが常に更新される

    //ネットワークから読み取った動画のリストをデータベースに挿入するメソッド、既にデータベース内に動画が登録されている場合、データベースを上書きする。
    //NOTE: REPLACE = 古いデータを置き換えてトランザクションを続行する OnConflict 戦略定数。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DatabaseVideo>)
}

@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase: RoomDatabase(){
    abstract val videoDao: VideoDao
}

//シングルトンオブジェクトを保持
private lateinit var INSTANCE: VideosDatabase

fun getDatabase(context: Context): VideosDatabase{
    //NOTE: 与えられたオブジェクトロックのモニタを保持したまま、与えられた関数ブロックを実行する。
    //public actual inline fun <R> synchronized(lock: Any, block: () -> R): R {
    //    contract {
    //        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    //    }
    synchronized(VideosDatabase::class.java){
        // .isInitializedというKotlinプロパティはlateinitプロパティ（ここではINSTANCEのこと）に値が代入されている場合にはtrueを、されていなければfalseを返します。
        if (!::INSTANCE.isInitialized){     //INSTANCEが初期化されていなければデータベースの構築をする
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                VideosDatabase::class.java,
                "videos"
            ).build()
        }
    }
    return INSTANCE
}
