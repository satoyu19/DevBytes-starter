レポジトリ
======================

レポジトリパターン
------------

レポジトリーパターンとは、アプリの他の部分からデータソースを切り離すデザインパターンのことです。
これにより、データにアクセスするために使うアプリの他の部分にクリーンなAPIを提供することが可能になります。

キャッシング(キャッシュ)
--------------

アプリがネットワークからデータを読み取った後、端末のストレージにデータを保存することで、データをキャッシュすることができます。
後で端末がオフラインのときにデータにアクセスしたり、何度も同じデータにアクセスしたいときなどにキャッシュします。

###Roomデータベースを利用してキャッシュ<br>
アプリに Room データベースを追加して、オフライン キャッシュとして使用する。
https://codelabsjp.net/wp-content/uploads/2021/01/388.png
Getting Started
---------------

1. Download and run the app.
2. You need Android Studio 3.2 or higher to build this project.

License
-------

Copyright 2019 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
