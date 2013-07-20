FN社の5.7x28mm弾使用銃器追加MOD FN5728 1.6.2 Rev3

射撃武器としてFN Five-seveNとFN P90っぽいのをを追加します。
littleMaidMob対応射撃兵装サンプル。



利用条件
	・効果音の製作者であるhiro35氏に感謝の祈りを捧げて下さい。
	・動画等での使用、改造、転載すきにしてもよいのよ？
	・ただし、商用利用は除く。
	・あと、いかなる意味でも作者は責任をとりませぬ。



使い方
	要Modloader
	要AudioMod(IBXMを使用しないのであれば不要)
	要MMMLib
	zipファイルを解凍して出来たフォルダを以下に放り込めば動くはずです。
	/mods/の中身を%appdata%/.minecraft/versions/1.6.2ML/mods/へ
	/assets/の中身を%appdata%/.minecraft/assets/へ
	%appdata%/.mincraft/config/mod_FN5728Guns.cfgができるので設定はそちらで。



効能
	・Five-seveNとP90、その弾薬であるSS190が作成できるようになります。
	・Five-seveNはセミオートで、弾薬が尽きるまで右クリックで撃つ事が出来ます。
	・Five-seveNは右プレス＆ホールドをした時間に応じて集弾性が上がります。
	・P90はフルオートで、弾薬が尽きるまで右プレス＆ホールドで撃つ事が出来ます。
	・弾薬が切れたら、一度クリックをやめてから右プレス＆ホールドでリロードを行ってください。
	・弾薬が残っている状態でも、左クリックをしながら右プレス＆ホールドでタクティカルリロードが出来ます。
	・リロードするときはインベントリに弾薬を入れておいて下さい。
	・ダメージ表示はマガジン内の残弾を表しています。



レシピ
	5.7x28mm SS190	x 16
		I
		G
		G

	Five-seveN	: Max Ammo 20
		III
		  I

	P90		: Max Ammo 50
		I
		III
		III

	I: IronIngot
	G: Gunpowder



注意
	・バックアップはこまめに取りましょう。
	・アイテムIDはデフォルトで22240 - 22242です。
	・アイテム追加系MODなのでバージョンアップ前にはこのMODで作成したアイテムは消しておきましょう。
	・アイテムIDを0以下に設定すると無効化されます。
	・P90の連射速度は600発/分と実銃の2/3です。
	・一度リロードに入ったら左クリックは離してもかまいません。
	・レシピが素材的におかしいのは仕様です、だってプラスチックとかないし。
	・発射音に関してはhiro35氏提供の大きい音版(fnp90_s_a)と小さい音版(fnp90_s_b)を同梱しています。
	・デフォルトでは小さい音版に設定してありますので、物足りない人はファイル名を変更して差し替えてください。
	・板ガラスを割ってしまいますのでインドアアタックをする場合は気をつけてください。
	・鉢植を割ってしまいますので、塀の上に並べて射撃練習とかすると雰囲気出ます。
	・TNTを起爆してしまうので危険物貯蔵庫でのミッションを行う場合には気をつけて下さい。
	・デフォルトでは被ダメージ時の無敵時間を無視するようになりましたのでフレンドリーファイアには注意してください。



効果音
	「.minecraft/resources/mod/sound/FN5728/」へ特定名称の音声ファイルを配置すれば効果音として使用されます。
	・弾の発射音				: fnP90_s
	・Blockへの着弾音			: bullethitBlock
	・Entityへの着弾音			: bullethitEntity
	・Five-seveNの空撃ち音			: emptyFive-seveN
	・Five-seveNのマガジンリリース音	: releaseFive-seveN
	・Five-seveNのマガジンロード音		: reloadFive-seveN
	・P90の空撃ち音				: emptyP90
	・P90のマガジンリリース音		: releaseP90
	・P90のマガジンロード音			: reloadP90



開発者向けのお話
	このMODはlittleMaidMobで特殊な動作をさせる為のサンプルコードを含んでいます。
	ソースコードを参照して組み込んでみてください。
	※現在ソースが若干変わっているため、この通りではありませんのでご注意下さい。

	・渡されたEntityから欲しい情報を引き出す。
		EntityLittleMaid	extends EntityAnimal
		EntityLittleMaidAvatar	extends EntityPlayer
		InventoryLittleMaid	extends InventoryPlayer


		EntityLittleMaidAvatar EntitylittleMaid.maidAvatarEntity
			射撃を行う際の身代わりEntity、これを使ってitemstack.useItemRightClick等を呼び出している。
			このEntityはisDeadが常にtrue。

		InventoryLittleMaid EntitylittleMaid.maidInventory
			メイドインベントリ、mainInventory[18]、armorInventory[4]。
			InventoryPlayerと完全互換。

		EntityLittleMaid EntityLittleMaidAvatar.avatar
			身代わりEntityの親となるEntityLittleMaidを保持している。

	・あると素敵なメソッド
		 littleMaidは使用するItemクラスに以下の関数がある場合値を参照します。
		 無い場合は通常の動作を行います。

		public boolean isWeaponReload(ItemStack itemstack, EntityPlayer entityplayer);
			アイテムがリロードを必要としている状態の時にTrueを返します。
			リロードが完了するまではTrueのまま値を固定してください。
			ターゲットをロックしていない状態でもリロードを行うようになります。

		public boolean isWeaponFullAuto(ItemStack itemstack);
			アイテムがフルオートで射撃を行う場合にTrueを返します。
			フルオートの定義はonPlayerStoppedUsingが呼ばれるまでの間にonUpdate等で射撃を実行するものです。
			射線軸判定の時に使用しています。

	・コーディング時の注意
		 itemstack.useItemRightClickを呼び出している都合上、
		飛翔体のEntityを作成する時にEntityLittleMaidAvatarが渡されることになります。
		このままEntityを作成すると撃ったLittleMaidとは違うEntityが実際の射撃を行うことになり、
		射撃位置や、射手の情報がおかしなことになってしまいます。
		 一応littleMaid側に回避処理を書いてはありますが完全ではないので、
		Entity作成時にEntityLittleMaidAvatar.avatarの値を使って処理を行う事を推奨します。
		 また、EntityLittleMaidAvatar.isDead=trueであるため、
		状況によってはこの部分で問題が発生することもあるようです。

	・実際のコード
		 引数としてEntityLittleMaidAvatarが渡された場合にEntityLittleMaidを取得する場合。
			try {
				// 射手の情報をEntityLittleMaidAvatarからEntityLittleMaidへ置き換える
				Field field = entityliving.getClass().getField("avatar");
				entityliving = (EntityLiving)field.get(entityliving);
			}
			catch (NoSuchFieldException e) {
			}
			catch (Exception e) {
			}

		引数としてEntityLittleMaidが渡された場合にEntityLittleMaidAvatarを取得する場合。
			try {
				// インベントリの情報を使いたいのでEntityLittleMaidAvaterへ置き換えている
				Field field = entity.getClass().getField("maidAvatarEntity");
				entity = (Entity)field.get(entity);
			}
			catch (NoSuchFieldException e) {
			}
			catch (Exception e) {
			}





変更点
	20130720.1	1.6.2 Rev3 更新
				MMMLibに併せて更新。



	20130717.1	1.6.2 Rev2 更新
				マルチ環境でメイドさんに撃たせると落ちるのを修正。
	20130713.1	1.6.2 Rev1 バージョンアップ
	20130606.1	1.5.2 Rev3 更新
				着弾表示の対策、浮かなくなりましたが他でおかしくなるかも？
				Infinityが付いている時の弱装化、具体的には威力半減。
	20130522.1	1.5.2 Rev2 更新
				Entityの登録方法の変更。
	20130507.1	1.5.2 Rev1 バージョンアップ
	20130425.1	1.5.1 Rev2 更新
				フルオートの動作周りを調整。
	20130403.1	1.5.1 Rev1 バージョンアップ
				MC-12133により着弾後の弾が浮きます。
				https://mojang.atlassian.net/browse/MC-12133
	20130131.1	1.4.7 Rev2 更新
				リロードシーケンスの調整。
				弾薬のクラスを分けた。
	20130116.1	1.4.7 Rev1 バージョンアップ
				鉢植が割れるようになりました。
	20121222.1	1.4.6 Rev1 バージョンアップ
	20121212.1	1.4.5 Rev4 修正
	20121204.1	1.4.5 Rev3 修正
				Forge環境下で弾道がおかしかったのを修正。
	20121202.1	1.4.5 Rev2 修正
				いくつかおかしかった部分を修正。
				MMMLibが必須に。
	20121121.1	1.4.5 Rev1 バージョンアップ
	20121120.1	1.4.4 Rev1 バージョンアップ
	20121113.1	1.4.2 Rev2 ちょこっと修正
	20121101.1	1.4.2 Rev1 バージョンアップ
	20120827.1	1.3.2 Rev3 修正
				Forge対応。
	20120827.1	1.3.2 Rev3 修正
				P90が正常に使えなかった問題を修正。
				リロードがおかしくなっていたのを修正。
	20120820.1	1.3.2 Rev1 バージョンアップ
	20120412.1	1.2.5 Rev1 バージョンアップ
	20120326.1	1.2.4 Rev1 バージョンアップ
	20120306.1	1.2.3 Rev2 修正
				ItemIDの計算方法が間違っていたので修正。
	20120305.1	1.2.3 Rev1 バージョンアップ
				弾のクラフト数を16へ戻した。
				クリエイティブの時は無限弾、リロード不要。
	20120217.1	1.1.0 Rev3 調整
				弾をSA無視のAP弾に。
				着弾音を設定できるようにした。
				効果音に変な変調がかかっていたのを修正。
	20120127.1	1.1.0 Rev2 調整、テキストの増量
	20120114.1	1.1.0 Rev1 リリース
	20120113.1	1.0.0 Rev1 リリース
