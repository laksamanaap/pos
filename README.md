# Aplikasi Kasir Sederhana (POS)

Aplikasi kasir sederhana berbasis Java Swing untuk manajemen penjualan toko.

## Fitur
1. **Manajemen Data Barang**: Tambah, Ubah, Hapus, Cari barang.
2. **Transaksi Penjualan**: Keranjang belanja, hitung total, kembalian, dan update stok otomatis.
3. **Laporan**: (Placeholder) Fitur laporan penjualan.
4. **Desain Modern**: Antarmuka pengguna yang bersih dan mudah digunakan.

## Cara Menjalankan

### Prasyarat
- Java Development Kit (JDK) versi 8 atau lebih baru.

### Kompilasi
Buka terminal/command prompt di direktori root project ini (`.../POS`), lalu jalankan perintah berikut untuk mengompilasi semua file Java:

```bash
javac -d bin src/com/pos/model/*.java src/com/pos/util/*.java src/com/pos/view/*.java src/com/pos/App.java
```

Pastikan folder `bin` sudah dibuat sebelumnya jika belum ada:
```bash
mkdir bin
```

### Menjalankan Aplikasi
Setelah berhasil dikompilasi, jalankan aplikasi dengan perintah:

```bash
java -cp bin com.pos.App
```

## Struktur Project
- `src/com/pos/model`: Class model data (Item).
- `src/com/pos/view`: Class tampilan GUI (MainFrame, ItemDataFrame, SalesTransactionFrame).
- `src/com/pos/util`: Class utilitas (DataManager, Style, GradientButton).
- `items.csv`: File penyimpanan data barang (dibuat otomatis saat aplikasi dijalankan).

## Catatan
- Data barang disimpan dalam file `items.csv`.
- Aplikasi ini menggunakan library standar Java Swing tanpa dependensi eksternal.
