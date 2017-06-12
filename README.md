# AndroidQuiz

Android解析の簡単な練習用に作成したアプリケーションです

アプリは
https://play.google.com/store/apps/details?id=com.totem.analyze_app_quiz
で公開してます

解析の練習用に使うだけのアプリなので、全然見た目とかは作ってません

ネタバレになりますが、以下が用意した解析ポイントです
* AndroidManifest.xmlをデコンパイルして中を見る
* リソース内の画像を見る
* ログ出力を見る
* 通信のキャプチャを行ってパケットを読む
* AES暗号化された画像をfetchしてるので、これをアプリ内にハードコートされてしまっている鍵とIVでdecryptするか、ヒープメモリをダンプして復号後の画像を抽出する

以上がroot化しなくても取れるフラグです

あと、隠されたフラグとして、  
sharedPrefsにputFlag:trueを記述するとフラグが得られるというものがあります  
SharedPrefsのモードとしてMODE_MULTI_PROCESSを指定しているので他のプロセスからアクセスできるため、root化しなくてもフラグを取得することができます  

JNIのC++では単純なフラグ隠蔽くらいしかしていないので、ちょっとアセンブラを読めば、上のような作業をせずともフラグは得られます(そっちの方が100%簡単)
