package com.dev_candra.mynotesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_candra.mynotesapp.R
import com.dev_candra.mynotesapp.adapter.NoteAdapter
import com.dev_candra.mynotesapp.database.NoteHelper
import com.dev_candra.mynotesapp.databinding.ActivityMainBinding
import com.dev_candra.mynotesapp.entity.Note
import com.dev_candra.mynotesapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object{
        private const val EXTRA_STATE = "extra_state"
    }

    private lateinit var adapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding

    private lateinit var noteHelper: NoteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null){
            // proses mengambil data
            loadNotesAsync()
        }else{
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null){
                adapter.listNotes = list
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportActionBar?.title = resources.getString(R.string.nama_developer)
        supportActionBar?.subtitle = resources.getString(R.string.note)

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity,NoteAddUpdateActivity::class.java)
            startActivityForResult(intent,NoteAddUpdateActivity.REQUEST_ADD)
        }

        /*
        Aturan utama dalam penggunaan dan akses database SQLite adalah membuat instance dan membuka koneksi pada metode onCreate():
         */
        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        loadNotesAsync()
    }

    private fun loadNotesAsync() {
        // Your Code
        /*
        Di sini kita menggunakan fungsi async karena kita menginginkan nilai kembalian dari fungsi yang kita panggil. Untuk mendapatkan nilai kembaliannya, kita menggunakan fungsi await().
        Fungsi ini digunakan untuk load data dari tabel dan dan kemudian menampilkannya ke dalam list secara asynchronous dengan menggunakan Background process seperti berikut.

         */
        GlobalScope.launch(Dispatchers.Main) {
            progressbar.visibility = View.VISIBLE
            val deferredNotes = async (Dispatchers.IO){
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0){
                adapter.listNotes = notes
            }else{
                adapter.listNotes = ArrayList()
                showSnackbarMessage(resources.getString(R.string.tidak_ada_data))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
        Setiap aksi yang dilakukan pada NoteAddUpdateActivity akan berdampak pada MainActivity baik itu untuk penambahan, pembaharuan atau penghapusan. Metode onActivityResult() akan melakukan penerimaan data dari intent yang dikirimkan dan diseleksi berdasarkan jenis requestCode dan resultCode-nya.
         */

        if (data != null){
            when(requestCode){
                // Akan dipanggil jika request codenya ADD

                /*
                Baris di atas akan dijalankan ketika terjadi penambahan data pada NoteAddUpdateActivity. Alhasil, ketika metode ini dijalankan maka kita akan membuat objek note baru dan inisiasikan dengan getParcelableExtra. Lalu panggil metode addItem yang berada di adapter dengan memasukan objek note sebagai argumen. Metode tersebut akan menjalankan notifyItemInserted dan penambahan arraylist-nya. Lalu objek rvNotes akan melakukan smoothscrolling, dan terakhir muncul notifikasi pesan dengan menggunakan Snackbar.

                 */

                NoteAddUpdateActivity.REQUEST_ADD ->if (resultCode == NoteAddUpdateActivity.RESULT_ADD){
                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)

                    showSnackbarMessage(resources.getString(R.string.item_berhasil))
                }

                // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
                /*
                Baris di atas akan dijalankan ketika terjadi perubahan data pada NoteAddUpdateActivity. Prosesnya hampir sama seperti ketika ada penambahan data, tetapi di sini kita harus membuat objek baru yaitu position. Sebabnya, metode updateItem membutuhkan 2 argumen yaitu position dan Note.
                 */
                NoteAddUpdateActivity.REQUEST_UPDATE ->
                    when(resultCode){
                        /*
                        Akan dipanggil jika result codenya UPDATE
                        Semua data di load kembali dari awal
                         */
                        NoteAddUpdateActivity.RESULT_UPDATE -> {
                            val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0)

                            adapter.updateItem(position,note)
                            binding.rvNotes.smoothScrollToPosition(position)

                            showSnackbarMessage(resources.getString(R.string.berhasil_diubah))

                        }

                        /*
                        Akan dipanggil jika result codenya DELETE
                        Delete akan menghapus data dari list berdasarkan dari position
                         */

                        /*
                        Baris di atas akan dijalankan jika nilai resultCode-nya adalah RESULT_DELETE. Di sini kita hanya membutuhkan position karena metode removeItem hanya membutuhkan position untuk digunakan pada notifyItemRemoved dan penghapusan data pada arraylist-nya.
                         */
                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0)

                            adapter.removeItem(position)

                            showSnackbarMessage(resources.getString(R.string.berhasil_dihapus))
                        }
                }
            }
        }
    }

    private fun showSnackbarMessage(string: String) {
        Snackbar.make(binding.rvNotes,string,Snackbar.LENGTH_SHORT).show()
    }

    /*
    Kemudian tutup koneksi pada metode onDestroy() (atau onStop()).

     */
    override fun onDestroy() {
        super.onDestroy()
        noteHelper.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE,adapter.listNotes)
    }
}