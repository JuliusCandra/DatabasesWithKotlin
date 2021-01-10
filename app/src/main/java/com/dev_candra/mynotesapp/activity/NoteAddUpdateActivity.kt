package com.dev_candra.mynotesapp.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dev_candra.mynotesapp.R
import com.dev_candra.mynotesapp.database.DatabaseContract
import com.dev_candra.mynotesapp.database.DatabaseContract.NoteColums.Companion.DATE
import com.dev_candra.mynotesapp.database.NoteHelper
import com.dev_candra.mynotesapp.databinding.ActivityNoteAddUpdateBinding
import com.dev_candra.mynotesapp.entity.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity(),View.OnClickListener{

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var noteHelper: NoteHelper

    companion object{
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    private lateinit var binding: ActivityNoteAddUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener(this)

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null){
            position = intent.getIntExtra(EXTRA_POSITION,0)
            isEdit = true
        }else{
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit){
            actionBarTitle = resources.getString(R.string.ubah)
            btnTitle = resources.getString(R.string.update)

            note?.let {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }
        }else{
            actionBarTitle = resources.getString(R.string.tambah)
            btnTitle = resources.getString(R.string.simpan)
        }
        initToolbar(actionBarTitle)
        binding.btnSubmit.text = btnTitle
    }

    private fun initToolbar(nama: String){
        supportActionBar?.title = resources.getString(R.string.nama_developer)
        supportActionBar?.subtitle = nama
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(p0: View?) {
        // Your Code
        if (p0?.id == R.id.btn_submit){
            handleClickView()
        }
    }

    private fun handleClickView(){
        val title = binding.edtTitle.text.toString()
        val description = binding.edtDescription.text.toString().trim()
        if (title.isEmpty()){
            binding.edtTitle.error = resources.getString(R.string.error)
            return
        }else if (description.isEmpty()){
            binding.edtDescription.error = resources.getString(R.string.error)
            return
        }

        note?.title = title
        note?.description = description

        val intent = Intent()
        intent.putExtra(EXTRA_NOTE,note)
        intent.putExtra(EXTRA_POSITION,position)

        val values = ContentValues()
        values.put(DatabaseContract.NoteColums.TITLE,title)
        values.put(DatabaseContract.NoteColums.DESCRIPTION,description)

        if (isEdit){
            // Upate data
            val result = noteHelper.update(note?.id.toString(),values).toLong()
            if (result > 0){

                /*
                Setiap aksi akan mengirimkan data dan RESULT_CODE untuk diproses pada MainActivity, contohnya seperti ini:
                 */

                setResult(RESULT_UPDATE,intent)
                finish()
            }else{
                Toast.makeText(this@NoteAddUpdateActivity,resources.getString(R.string.gagal),Toast.LENGTH_SHORT).show()
            }
        }else{
            // Add data
            /*
            Variable isEdit akan menjadi true pada saat Intent melalui kelas adapter, karena mengirimkan objek listnotes. Lalu pada NoteAddUpdateActivity akan divalidasi. Jika tidak null maka isEdit akan berubah true.
             */

            note?.date = getCurrentDate()
            values.put(DATE,getCurrentDate())
            val result = noteHelper.insert(values)

            if (result > 0){
                note?.id = result.toInt()
                setResult(RESULT_ADD,intent)
                finish()
            }else{
                Toast.makeText(this@NoteAddUpdateActivity,resources.getString(R.string.gagal_menambah_data),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String{
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    /*
    Ketika pengguna berada pada proses pembaruan data, setiap kolom pada form sudah terisi otomatis. Ikon untuk hapus di sudut kanan atas ActionBar berfungsi untuk menghapus data. Kode berikut akan menjalankan kebutuhan di atas. Intinya cek nilai boolean isEdit yang berasal dari proses validasi, apakah objek note berisi null atau tidak
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if (isEdit){
            menuInflater.inflate(R.menu.menu,menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int){
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose){
            dialogTitle = resources.getString(R.string.batal)
            dialogMessage = resources.getString(R.string.membatalkan_close)
        }else{
            dialogMessage = resources.getString(R.string.hapus_item)
            dialogTitle = resources.getString(R.string.hapus_note)
        }

        /*
        Pada proses penghapusan data, dialog konfirmasi tampil. Ia pun muncul ketika pengguna menekan tombol back baik pada ActionBar atau peranti. Dialog konfirmasi tersebut muncul sebelum menutup halaman. Untuk itu, gunakan fasilitas AlertDialog untuk menampilkan dialog.
        Di sini kondisikan dialog sesuai dengan kebutuhan, apakah untuk menampilkan dialog menutup halaman atau menghapus data?

         */
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder.setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ya)) {_,_ ->
                if (isDialogClose){
                    finish()
                }else{
                    val result = noteHelper.deleteById(note?.id.toString()).toLong()
                    if (result > 0){
                        val intent = Intent()
                        intent.putExtra(EXTRA_POSITION,position)
                        setResult(RESULT_DELETE,intent)
                        finish()
                    }else{
                        Toast.makeText(this@NoteAddUpdateActivity,resources.getString(R.string.menghapus_data),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.tidak)) {dialogInterface, _ -> dialogInterface.cancel()  }
        val alretDialog = alertDialogBuilder.create()
        alretDialog.show()
    }

}