
# Лабораторна робота №4
# &nbsp;Виконала студентка 3 курсу, групи ІО-21, Любченко Анна.
&nbsp;&nbsp;&nbsp; На тему :" Дослідження способів роботи з медіаданими"
Мета роботи: дослідити яким чином платформа Андроїд надає можливість оброблювати аудіо-файли та відео-файли та отримати практичні навички щодо використання інструментів відтворення медіа-даних.
Робота була виконана на оцінку 20, за повними вимогами.

Маємо 2 файли інтерфейсу activity_main.xml player_main.xml та логіки програми. MainActivity.kt PlayerAct.kt /

![image](https://github.com/user-attachments/assets/90263f82-ce64-4cfc-9683-db44bab51d21)

У яких відповідно описано функціонал програми за вимогами завдання.
Головний екран, який відкривається одразу при запуску програми має Radiogroup з RadioButton всередині для обирання того який файл буде обрано аудіо чи відео. Є поле EditText для введення URL, якщо користувач бажає завантажити дані з посилання. З самого низу 2 Button "Програти з Інтернету" та "Вибрати файл з пристрою". Відповідно від вибору користувача залежить поведінка застосунку далі.

Для відтворення аудіо застосовується MediaPlayer, а для відео ExoPlayer, під який треба було підлаштувати сам проект.
У build.gradle було додано залежність на ExoPlayer:

              dependencies {
                  implementation "androidx.appcompat:appcompat:1.6.1"
                  implementation 'com.google.android.exoplayer:exoplayer:2.18.7'
              }

Для коректної роботи до AndroidManifest.xml було додано нову актитвність (при переході до неї до цього виникала помилка і застосунок вилітав)

                <activity android:name=".PlayerAct"
                            android:exported="true"/>
                            
Також для відтворення відео/аудіо з інтернету було додано дозвіл на Інтернет у AndroidManifest.xml (до цього при натисканні на кнопку "Програти з Інтернету" при введеному URL було )

            <uses-permission android:name="android.permission.INTERNET" />


Розглянемо варіант, коли користувач обирає "Вибрати файл з пристрою" , від флагу в Radiogroup залежить який формат файлу буде доступний для обрання. Відповіно запускається Intent.

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = if (isVideo) "video/*" else "audio/*"
            startActivityForResult(intent, 100)

Повертається URI у функцію onActivityResult та передає її активності PlayerAct. 
Далі файл передається до відповідного програвача 
Аудіо:

              mediaPlayer = MediaPlayer().apply {
                  setDataSource(this@PlayerAct, mediaUri)
                  prepare()
                  start()
              }

Відео:

            player = ExoPlayer.Builder(this).build().also {
                playerView.player = it
                it.setMediaItem(MediaItem.fromUri(mediaUri))
                it.prepare()
                it.play()
            }

Про роботу кнопок трохи пізніше, після розгляду роботи програми після натискання на кнопку "Програти з Інтернету". 

Розглянемо варіант, коли користувач обирає "Програти з Інтернету"
Першочергово програма бере посилання на конкретний файл аудіо/відео з EditText та передає його до активності PlayerAct через Intent. Тип медіа так само залежить від прапорця в Radiogroup. (перевіряється булевим is Video = true/false)

              findViewById<Button>(R.id.playFromUrlButton).setOnClickListener {
                          val url = urlInput.text.toString()
                          val intent = Intent(this, PlayerAct::class.java)
                          intent.putExtra("url", url)
                          intent.putExtra("isVideo", typeGroup.checkedRadioButtonId == R.id.videoButton)
                          startActivity(intent)
                      }

Якщо isVideo = true — буде використано ExoPlayer (для відео). Інакше — MediaPlayer.
ExoPlayer сам вміє працювати з HTTP(s) URI, таким чином ми можемо без додавання додаткових бібліотек. Він розроблений Google, спеціально для Android.

Кнопки Play, Pause, Stop. Працюють таким чином, що є змінна , яка оголошується на початку коду -lastPosition, саме від неї залежить відтворення медіа після зупинки з моменту де воно було зупинено. При натисканні на кнопку Play - створюється програвач і починається відтворення, після натискання на кнопку Pause медіа зупиняється і дані записуються до lastPosition. Якщо користувач бажає продовжити гру - він натискає на Play і медіа відтворюється з поменту зупинки. Stop працює таким чином, що повністю зупиняє відтворення якщо відео - звільнення ресурсів (release()), обнулення player, якщо аудіо -  звільнення (release()), обнулення mediaPlayer. Якщо користувач бажає знову запустити відтворення, треба знову натиснути на Play , і буде створено новий програвач.

### Демонстрація роботи

[![Відео з Google Drive](https://i.imgur.com/preview.jpg)](https://drive.google.com/file/d/ID_ФАЙЛУ/view?usp=sharing)


              


