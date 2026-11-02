#FlappyBird Game (Java Swing)
JANJI:
Saya Nadya Rizqitha Salsabila dengan NIM 2500991 mengerjakan Tugas Praktikum 6
dalam mata kuliah Desain Pemrograman Berorientasi Objek untuk keberkahanNya
maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
Project Praktikum Desain Pemrograman Berbasis Objek (DPBO).

#Spesifikasi Wajib Terimplementasi
1. Game Over: Berhenti saat menabrak pipa atau menyentuh dasar layar.
2. Restart Game: Menekan tombol `R` pada keyboard saat layar Game Over.
3. Score Label: `JLabel` di bagian atas layar untuk menampilkan skor aktual.
4. Penambahan Skor: +1 poin saat berhasil melewati setiap pasang pipa.
5. Main Menu: Menu awal dengan tombol Start Game dan Exit Game.

#Fitur Tambahan:
- Sprite burung baru (4 frame animasi sayap) dan background hutan saat game dimainkan.
- Tombol 3 garis (kotak, kiri atas layar game) berisi: Home (kembali ke main menu), Menu (tutup menu & lanjut main), Quit (keluar).
- History Score disimpan di `history.txt`, tampil di main menu dan saat Quit.
- Setelah `R` (restart) atau Start Game, burung diam dulu sampai `SPASI` ditekan.
- Tombol Background di main menu untuk memilih background: Hutan, Gunung, Jungle, Air Terjun (beranimasi).
- Sound: efek sayap (SPASI), "ting" saat skor naik, "plak" saat menabrak/jatuh, plus backsound pelan (music1-3.wav) sejak main menu.

#Penjelasan Desain Program:
| Peran | Kelas | Tanggung jawab |
|-------|-------|----------------|
| Model | `Player`, `Pipe` | Menyimpan posisi (x, y), ukuran, gambar, dan kecepatan. Hanya getter/setter, tanpa logika game |
| Model | `ScoreHistory` | Membaca dan menulis riwayat skor ke `history.txt` (format `skor;tanggal jam`), menghitung skor terbaik, dan membentuk teks tampilan |
| Controller | `Logic` | Inti game: game loop, gravitasi, pergerakan pipa, deteksi tabrakan, skor, state game, input keyboard (`KeyListener`) |
| Controller | `SoundManager` | Memutar efek suara dan backsound (`javax.sound.sampled`) |
| View | `View` | `JPanel` yang menggambar background, burung, pipa, dan teks (Game Over, "Tekan SPASI untuk mulai") |
| View | `MainMenu` | `JPanel` menu awal: judul, tombol Start / Background / Exit, dan history score |
| Main | `App` | Membuat `JFrame`, menyusun layar dengan `CardLayout`, menghubungkan semua kelas, popup 3 garis, dialog background, dan proses keluar |

#Struktur Folder Project:
FlappyBird/
├── src/
│   ├── assets/
│   │   ├── bird0.png ... bird3.png (animasi sayap)
│   │   ├── bg_forest.png, bg_mountain.png, bg_tileset.png, bg_waterfall0-7.png
│   │   ├── upperPipe.png
│   │   └── lowerPipe.png
│   ├── App.java
│   ├── Logic.java
│   ├── MainMenu.java
│   ├── Pipe.java
│   ├── Player.java
│   ├── ScoreHistory.java
│   └── View.java
├── Dokumentasi/
└── README.md

#Hubungan antar kelas
classDiagram
    App --> MainMenu : membuat
    App --> View : membuat
    App --> Logic : membuat
    Logic --> Player : memiliki 1
    Logic --> Pipe : memiliki banyak
    Logic --> ScoreHistory : memiliki
    Logic --> SoundManager : memiliki
    View --> Logic : membaca data untuk digambar
    MainMenu --> ScoreHistory : menampilkan riwayat
    View ..> Player : menggambar
    View ..> Pipe : menggambar

##Penjelasan Alur
a. Alur program secara keseluruhan
flowchart TD
    A[Program dibuka] --> B[Main Menu + backsound mulai]
    B -->|Start Game| C[Layar game, state Menunggu]
    B -->|Background| BG[Dialog pilih background] --> B
    B -->|Exit Game| Q[Simpan skor, tampilkan history, keluar]
    C -->|SPASI pertama| D[Bermain]
    D -->|Menabrak / jatuh| E[Game Over]
    E -->|R| C
    D -->|Tombol 3 garis| M[Jeda + popup]
    M -->|Menu| D
    M -->|Home| B
    M -->|Quit| Q

b. Alur di dalam game loop (tiap frame, 60x per detik)
`Logic.actionPerformed()` memanggil `move()` lalu `view.repaint()`.

1. Ganti frame sayap burung (animasi).
2. Jika menunggu atau jeda: berhenti di sini (tidak ada pergerakan).
3. Terapkan gravitasi: `velocityY += gravity`, lalu `posY += velocityY`. Burung dijaga agar tidak keluar dari batas atas.
4. Jika kaki burung menyentuh dasar layar, panggil `triggerGameOver()`.
5. Untuk setiap pipa:
   - Geser ke kiri sejauh `pipeVelocityX`.
   - Jika burung sudah melewati sisi kanan pipa atas dan belum dihitung, tandai `passed`, tambah skor +1, perbarui `JLabel`, dan bunyikan "ting".
   - Jika kotak burung beririsan dengan kotak pipa, panggil `triggerGameOver()`.
6. `View` menggambar ulang: background, pipa, burung, lalu teks bantuan atau Game Over.

Pipa baru dibuat oleh `pipesCooldown` (tiap 1500 ms) lewat `placePipes()`: posisi pipa atas dibuat acak, lalu pipa bawah ditempatkan di bawahnya dengan celah tetap. Timer ini baru jalan setelah SPASI pertama.

c. Alur input keyboard (`Logic.keyPressed`)
- **SPASI**: abaikan jika Game Over atau jeda. Jika game belum mulai, set `started = true` dan jalankan `pipesCooldown`. Lalu burung melompat (`velocityY = -10`) dan bunyi sayap diputar.
- **R**: hanya bekerja saat Game Over, memanggil `restartGame()`.

d. Alur Game Over dan Restart
- `triggerGameOver()`: bunyikan "plak", simpan skor ke `history.txt` (hanya sekali), hentikan `gameLoop` dan `pipesCooldown`.
- `restartGame()`: kembalikan burung ke posisi awal dan kecepatan 0, hapus semua pipa, reset skor dan label, lalu masuk state Menunggu. Burung tidak bergerak dan pipa belum muncul sampai pemain menekan SPASI, sehingga pemain punya waktu berpindah dari `R` ke SPASI.

e. Alur menu 3 garis
Saat popup terbuka, `Logic.pause()` dipanggil. Saat popup tertutup, `Logic.resume()` dipanggil dan fokus keyboard dikembalikan ke `View`.
- **Home**: simpan skor jika sedang bermain (skor > 0), reset game, kembali ke Main Menu, dan perbarui history.
- **Menu**: menutup popup dan melanjutkan permainan.
- **Quit**: keluar lewat alur keluar di bawah.

f. Alur pilih background
Tombol *Background* membuka dialog dengan 4 gambar kecil. Saat salah satu diklik, `View.setSelectedBackground(index)` dipanggil dan dialog menutup. Pada `View.draw()`, frame background dipilih dengan waktu (`System.currentTimeMillis() / 100`), jadi background Air Terjun (8 frame) berganti tiap 100 ms, sedangkan yang lain hanya punya 1 frame. Pilihan tidak disimpan setelah program ditutup.

g. Alur history score
- Skor disimpan saat Game Over, atau saat pemain keluar / pulang ke Home di tengah permainan dengan skor lebih dari 0.
- Setiap penyimpanan menambah satu baris di `history.txt`, misalnya `7;02/11/2026 12:30`.
- Main Menu menampilkan 5 skor terakhir. Dialog saat keluar menampilkan 10 skor terakhir dan skor terbaik.

h. Alur suara
- `App` memanggil `startMusic()` setelah jendela tampil, jadi backsound mulai dari Main Menu dan tidak terputus saat pindah ke layar game.
- Lagu diputar berurutan `music1 → music2 → music3` lalu mengulang. Volumenya diturunkan (`MUSIC_GAIN_DB = -22`) agar efek suara lebih jelas.
- Setiap efek suara memakai `Clip` baru yang ditutup otomatis setelah selesai, sehingga beberapa efek bisa berbunyi bersamaan.
- Jika komputer tidak punya perangkat audio, error ditangkap dan game tetap berjalan tanpa suara.

i. Alur keluar program
`Exit Game`, `Quit`, maupun tombol X pada jendela menuju `App.quit()`: simpan skor jika perlu, hentikan musik, tampilkan dialog history score, lalu `System.exit(0)`.

#Dokumentasi:
![alt text](<Dokumentasi/Screenshot 2026-11-02 200233.png>)
![alt text](<Dokumentasi/Screenshot 2026-11-02 200241.png>)
![alt text](<Dokumentasi/Screenshot 2026-11-02 200251.png>)
![alt text](<Dokumentasi/Screenshot 2026-11-02 200310.png>)
![alt text](<Dokumentasi/Screenshot 2026-11-02 200317.png>)
https://youtube.com/shorts/XQBw-oufSXw?feature=share