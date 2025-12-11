# Aplikasi Kasir Sederhana (POS)

Aplikasi kasir sederhana berbasis Java Swing untuk manajemen penjualan toko dengan penyimpanan data di PostgreSQL.

## Fitur
1. **Manajemen Data Barang**: Tambah, Ubah, Hapus, Cari barang
2. **Harga Jual Otomatis**: Harga jual dihitung otomatis berdasarkan harga beli dan persentase laba global
3. **Transaksi Penjualan**: Keranjang belanja, hitung total, kembalian, dan update stok otomatis
4. **Laporan Penjualan**: Lihat riwayat penjualan dan detail transaksi
5. **Pengaturan Global**: Atur persentase laba default untuk semua produk
6. **Export Excel**: Export laporan penjualan dalam format .xlsx
7. **Desain Modern**: Antarmuka pengguna yang bersih dan mudah digunakan dengan Nimbus L&F

## Prasyarat

### Software yang Diperlukan
- **Java Development Kit (JDK)** versi 8 atau lebih baru
- **PostgreSQL** versi 10 atau lebih baru
- Database sudah berjalan dan bisa diakses

### Library Eksternal
Aplikasi menggunakan library berikut (sudah disertakan di folder `lib/`):
- `postgresql-42.7.0.jar` - PostgreSQL JDBC driver
- `poi-5.2.3.jar` - Apache POI untuk export Excel
- `poi-ooxml-5.2.3.jar` - Apache POI OOXML support
- `xmlbeans-5.1.1.jar` - XML parsing untuk POI

## Konfigurasi Database

### 1. Buat Database PostgreSQL
```sql
CREATE DATABASE posdb;
```

### 2. Buat File `db.properties`
Buat file `db.properties` di root folder project dengan isi:

```properties
url=jdbc:postgresql://localhost:5432/posdb
user=postgres
password=yourpassword
```

**Ganti `yourpassword` dengan password PostgreSQL Anda.**

### 3. Jalankan Script Database (Opsional)
Script `schema.sql` sudah tersedia untuk membuat tabel secara otomatis, namun aplikasi akan membuat table jika belum ada.

## Cara Menjalankan

### Kompilasi
Buka terminal/command prompt di direktori root project, lalu jalankan:

```bash
javac -source 1.8 -target 1.8 -cp ".;bin;src;lib/*" -d bin src/com/pos/**/*.java
```

Atau di Linux/Mac:
```bash
javac -source 1.8 -target 1.8 -cp ".:bin:src:lib/*" -d bin src/com/pos/**/*.java
```

### Menjalankan Aplikasi
Setelah kompilasi berhasil:

```bash
java -cp "bin;lib/*" com.pos.App
```

Atau di Linux/Mac:
```bash
java -cp "bin:lib/*" com.pos.App
```

## Struktur Project
```
pos/
├── src/
│   └── com/pos/
│       ├── App.java
│       ├── model/
│       │   ├── Item.java
│       │   └── SalesDetail.java
│       ├── util/
│       │   ├── DataManager.java (PostgreSQL-only)
│       │   ├── SalesManager.java (PostgreSQL-only)
│       │   ├── SettingsManager.java (PostgreSQL-only)
│       │   ├── Style.java
│       │   ├── UIUtils.java
│       │   ├── GradientButton.java
│       │   └── SolidButton.java
│       └── view/
│           ├── MainFrame.java
│           ├── ItemDataFrame.java
│           ├── SalesTransactionFrame.java
│           ├── SalesReportFrame.java
│           ├── ReceiptDialog.java
│           └── SettingsFrame.java
├── lib/
│   ├── postgresql-42.7.0.jar
│   ├── poi-5.2.3.jar
│   ├── poi-ooxml-5.2.3.jar
│   └── xmlbeans-5.1.1.jar
├── db.properties (HARUS DIBUAT)
├── schema.sql
└── README.md
```

## Fitur Utama

### 1. Manajemen Barang
- Tambah barang baru dengan kode, nama, harga beli, dan stok
- Harga jual dihitung otomatis: `Harga Jual = Harga Beli × (1 + Persentase Laba / 100)`
- Edit dan hapus barang yang sudah ada
- Cari barang berdasarkan kode atau nama

### 2. Pengaturan Global (Settings)
- Atur persentase laba default untuk semua produk
- Default: 20%
- Disimpan di database PostgreSQL table `profit_margin_settings`

### 3. Transaksi Penjualan
- Pilih item dari daftar barang
- Tambahkan ke keranjang dengan jumlah yang diinginkan
- Lihat total dan hitung kembalian otomatis
- Stok barang otomatis berkurang saat transaksi selesai

### 4. Laporan Penjualan
- Lihat riwayat semua transaksi
- Lihat detail item pada setiap transaksi
- Filter transaksi berdasarkan range tanggal (opsional)

### 5. Export Excel
- Export laporan penjualan dalam format .xlsx
- Formatting profesional dengan header, border, dan alignment

## Troubleshooting

### Error: "db.properties not found"
**Solusi**: Pastikan file `db.properties` sudah dibuat di root folder project dengan konfigurasi database yang benar.

### Error: "Failed to connect to database"
**Solusi**: 
1. Periksa apakah PostgreSQL sudah berjalan
2. Periksa konfigurasi di `db.properties` (url, user, password)
3. Pastikan database `posdb` sudah dibuat

### Error: "PostgreSQL JDBC driver not found"
**Solusi**: Pastikan file `postgresql-42.7.0.jar` ada di folder `lib/` dan classpath saat compile dan run sudah benar.

### Error: "relation 'items' does not exist"
**Solusi**: Aplikasi akan membuat table secara otomatis saat pertama kali dijalankan. Jika error tetap muncul, jalankan `schema.sql` secara manual di PostgreSQL.

## Catatan Penting
- **Aplikasi HANYA menggunakan PostgreSQL** untuk penyimpanan data. CSV fallback sudah dihapus.
- Pastikan database PostgreSQL terkoneksi sebelum menjalankan aplikasi.
- Data barang, transaksi, dan pengaturan semuanya disimpan di database PostgreSQL.
- Perubahan harga jual otomatis saat input harga beli di form Manajemen Barang.
