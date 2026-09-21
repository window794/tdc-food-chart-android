package com.broco.tdc_food_chart.ui.help

import com.broco.tdc_food_chart.ui.collection.CollectionSegment
import com.broco.tdc_food_chart.ui.home.HomeTab

/** ヘルプ項目から関連画面へ移動する導線。実装済みの画面だけを指す。 */
sealed interface HelpAction {
    val label: String

    /** ホームの指定タブへ（コレクションはセグメントも指定できる）。 */
    data class OpenTab(
        override val label: String,
        val tab: HomeTab,
        val segment: CollectionSegment? = null,
    ) : HelpAction
}

/** ヘルプの 1 セクション。見出し・短い箇条書き・任意の導線。 */
data class HelpSection(
    val id: String,
    val title: String,
    val english: String,
    val items: List<String>,
    val action: HelpAction? = null,
)

/**
 * 「使い方 / ヘルプ」の本文。アプリ内に持ち、ネット接続なしで読める。
 * オンボーディング（概要）より一段詳しい「操作の説明」に徹する。1 項目は 1〜2 行。
 */
object HelpContent {
    val sections: List<HelpSection> = listOf(
        HelpSection(
            id = "search",
            title = "探す",
            english = "Search",
            items = listOf(
                "検索バーに入力すると、メニュー名・店舗名・考案者・エリアで部分一致検索します。",
                "「絞り込み」でエリア・コースター種別・考案者を選べます。閉店した店舗を含めるかもここで切り替えられます。",
                "「未食のみ」を押すと、まだ食べていないフードだけが並びます。",
                "「エリア順」を押すと並び替えを選べます（エリア順／安い順／高い順）。",
                "一覧の行を押すとフード詳細が開きます。行の右端の ☆ でその場でお気に入りにできます。",
            ),
            action = HelpAction.OpenTab("探すタブへ", HomeTab.SEARCH),
        ),
        HelpSection(
            id = "favorite",
            title = "お気に入り",
            english = "Favorites",
            items = listOf(
                "一覧の ☆ か、フード詳細の「☆ お気に入り」で登録・解除できます。",
                "登録したフードはコレクションの「お気に入り」に並びます。",
            ),
            action = HelpAction.OpenTab("お気に入りを見る", HomeTab.COLLECTION, CollectionSegment.FAVORITE),
        ),
        HelpSection(
            id = "eaten",
            title = "食べた記録",
            english = "Eaten",
            items = listOf(
                "フード詳細の「食べた」を ON にすると、観測記録とコレクションに反映されます。",
                "ON にした日が「食べた日」として入ります。「変更」を押すと任意の日付に変えられます。",
                "「食べた」を OFF にしても、食べた日・メモ・写真は消えません。もう一度 ON にすると以前の日付が戻ります。",
            ),
            action = HelpAction.OpenTab("食べたフードを見る", HomeTab.COLLECTION, CollectionSegment.EATEN),
        ),
        HelpSection(
            id = "photo",
            title = "写真",
            english = "Photos",
            items = listOf(
                "フード詳細の「写真を追加」から、端末の写真を選んで保存できます（複数枚まとめて選べます）。",
                "保存した写真はこのアプリの中にコピーされます。元の写真をギャラリーから消しても、ここには残ります。",
                "写真に撮影日時が記録されていれば、食べた日がまだ無いときに「撮影日を食べた日にしますか？」と確認します。すでに食べた日がある場合は上書きしません。",
                "サムネイルを押すと拡大表示できます。拡大画面の「削除」で、確認のうえ写真を削除できます。",
                "写真の選択に、写真ライブラリ全体へのアクセス権限は使いません。",
            ),
        ),
        HelpSection(
            id = "memo",
            title = "メモ",
            english = "Notes",
            items = listOf(
                "フード詳細の「Note」欄に書くと、そのフードのメモとして自動で保存されます。",
                "メモのあるフードはコレクションの「メモ」に並び、一覧に 1 行だけ抜粋が出ます。",
            ),
            action = HelpAction.OpenTab("メモ付きフードを見る", HomeTab.COLLECTION, CollectionSegment.MEMO),
        ),
        HelpSection(
            id = "collection",
            title = "コレクション",
            english = "Collection",
            items = listOf(
                "上部の「お気に入り／食べた／メモ」で切り替えます。数字はそれぞれの件数です。",
                "ここに並ぶのは自分の記録なので、閉店設定に関係なく表示されます（閉店バッジは付きます）。",
            ),
            action = HelpAction.OpenTab("コレクションへ", HomeTab.COLLECTION),
        ),
        HelpSection(
            id = "observation",
            title = "観測記録",
            english = "Observation log",
            items = listOf(
                "「Charted」に食べた数 ／ 全料理数と、達成率・残り数が出ます。",
                "「By area」でエリア別の進捗、「Latest」で最近食べた記録（食べた日の新しい順）を確認できます。",
                "「Latest」の行を押すと、そのフードの詳細が開きます。",
            ),
            action = HelpAction.OpenTab("観測記録を見る", HomeTab.OBSERVATION),
        ),
        HelpSection(
            id = "closed",
            title = "閉店したフード",
            english = "Closed",
            items = listOf(
                "閉店した店舗のフードには「閉店」と表示し、詳細には「掲載当時の情報です」と注記します。",
                "設定タブ →「閉店したフードを表示」で、探すタブの一覧に含めるかを切り替えられます。絞り込みシートの「閉店した店舗のフードも含める」と同じ設定です。",
            ),
        ),
        HelpSection(
            id = "offline",
            title = "オフライン・保存場所",
            english = "Offline & storage",
            items = listOf(
                "料理データはアプリの中に入っているので、検索・絞り込み・記録の確認は電波のない場所でもできます。",
                "お気に入り・食べた記録・メモ・写真はこの端末の中にだけ保存され、外部のサーバーへは送りません。",
                "ネット接続が必要なのは、フード詳細の「店舗ページを開く」だけです（外部ブラウザで開きます）。",
                "アプリをアンインストールすると、記録と写真も一緒に消えます。",
            ),
        ),
    )
}
