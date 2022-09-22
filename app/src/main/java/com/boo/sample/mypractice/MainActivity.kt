package com.boo.sample.mypractice

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Editable
import android.transition.TransitionManager
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.boo.sample.mypractice.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.lang.IllegalStateException
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

class MainActivity : AppCompatActivity() {
    val TAG = this::class.java.simpleName
    val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }

    val compositDisposable = CompositeDisposable()
    val mRecyclerViewAdapter by lazy { RecyclerViewAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.btnWrite?.setOnClickListener {
            val internalPath = filesDir.absolutePath    //internal storage
            val writePath = "$internalPath/writeFile.txt"
            Log.d(TAG, "writePath: $writePath")

            writeTextToFile(writePath)

        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//            val getStorageDirectory = Environment.getStorageDirectory().absolutePath
//            println("publicPath is $getStorageDirectory")
//            val fileList = Environment.getStorageDirectory().list()
//            println("show File List: $fileList")
//
//        }

//        //공유저장소 디렉토리 생성
//        val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//        fileLauncher.launch(fileIntent)
//
//        //공유저장소 파일 쓰기
//        val fileName = "NewImage.jpg"
//        val makeFileIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "image/jpg"
//            putExtra(Intent.EXTRA_TITLE, fileName)
//        }
//        makeFileLauncher.launch(makeFileIntent)
//
//        val deleteIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "image/*"
//        }
//        deleteFileLauncher.launch(deleteIntent)

        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.list_item, R.id.textView, items)
        (binding.textField?.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        val samples = listOf("banana", "orange", "apple", "apple mango", "melon", "watermelon")

        binding.testBtn?.setOnClickListener {
//            startRx()
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        getObservable()
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { s -> s.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ value ->
                println("Search $value")
            }, {
                it.printStackTrace()
            }).also { compositDisposable.add(it) }

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView?.apply {
            this.layoutManager = layoutManager
            this.adapter = mRecyclerViewAdapter
            mRecyclerViewAdapter
                .getItemPublishSubject()
                .subscribe { s ->
                    Toast.makeText(this@MainActivity, s.title, Toast.LENGTH_SHORT).show()
                }
                .also { compositDisposable.add(it) }
        }

        val ob2 = Observable.just("polling #2")
            .repeatWhen { observable ->
                observable.delay(3, TimeUnit.SECONDS)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { println(it) }
    }

    private fun startRx() {
        val service = RestfulAdapter.getServiceApi()
        val observable = service.getObContributors("android", "RxJava")

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ contributors ->
                for(c in contributors){
                    println(c.toString())
                }
            }, {
                it.printStackTrace()
            }).also { compositDisposable.add(it) }
    }

    val fileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            Log.d(TAG, "result is RESULT_OK")

            val uri = result.data?.data
            Log.d(TAG, "uri is $uri")

            val takeFlags = (intent.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                val folderDoc = DocumentFile.fromTreeUri(this, uri)
                folderDoc?.createDirectory("BooDir")
            }
        }
    }

    val makeFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if(uri != null){
                contentResolver.openFileDescriptor(uri, "w").use {
                    FileOutputStream(it!!.fileDescriptor).use { outStream ->
                        val imageInputStream = resources.openRawResource(R.raw.app_icon)
                        while(true){
                            val data = imageInputStream.read()
                            if(data == -1) {
                                break
                            }
                            outStream.write(data)
                        }
                        imageInputStream.close()
                    }
                }
            }

        }
    }

    val deleteFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            uri?.let {  deleteFile(uri) }
        }
    }

    fun deleteFile(uri: Uri) {
        DocumentsContract.deleteDocument(contentResolver, uri)
    }

    override fun onStart() {
        super.onStart()
        getItemObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { item ->
                mRecyclerViewAdapter.updateItem(item)
                mRecyclerViewAdapter.notifyDataSetChanged()
            }.also { compositDisposable.add(it) }

        //Thread.sleep(3000)
    }

    fun getObservable(): Observable<String> = Observable.create { emitter ->
        binding.no3?.editText?.doOnTextChanged { inputText, _, _, _ ->
            Log.d(TAG, "onTextChange $inputText")
            emitter.onNext(inputText?.toString()!!)
        }
    }

    fun getItemObservable(): Observable<RecyclerItem> {
        val pm = packageManager
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        return Observable.fromIterable(pm.queryIntentActivities(i, 0))
            .doOnNext { println("doOnNext $it") }
            .sorted(ResolveInfo.DisplayNameComparator(pm))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { item ->
                val image = item.activityInfo.loadIcon(pm)
                val title = item.activityInfo.loadLabel(pm).toString()
                RecyclerItem(image, title)
            }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view = currentFocus
        if (ev?.action == MotionEvent.ACTION_DOWN && view is EditText) {
            val outRect = Rect()
                .also {
                    view.getGlobalVisibleRect(it)
                }
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                view.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun animateToKeyframeTwo() {
        val constraintSet = ConstraintSet()
        constraintSet.load(this, R.layout.keyframe2)
        TransitionManager.beginDelayedTransition(binding.constLayout)
        constraintSet.applyTo(binding.constLayout)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositDisposable.dispose()
    }

    private fun writeTextToFile(path : String) {
        val file = File(path)
        val fileWriter = FileWriter(file, false)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.append("Test1\n")
        bufferedWriter.append("Test2")
        bufferedWriter.newLine()
        bufferedWriter.append("Test3\n")
        bufferedWriter.close()
    }

}

class LocalVolley constructor(context: Context) {
    val TAG = this::class.java.simpleName
    var sRequestQueue: RequestQueue

    init {
        sRequestQueue = Volley.newRequestQueue(context)
    }

    fun getRequestQueue() =
        if (sRequestQueue != null) sRequestQueue else throw IllegalStateException("Not inited")

    private fun getFuture(): RequestFuture<JSONObject> {
        val future = RequestFuture.newFuture<JSONObject>()
        val req = JsonObjectRequest("http://time.jsontest.com/", null, future, future)
        getRequestQueue().add(req)
        return future
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun getData(): JSONObject {
        return getFuture().get()
    }

    fun getObservable() : Observable<JSONObject> {
        return Observable.defer {
            try {
                Observable.just(getData())
            } catch (e: InterruptedException) {
                Log.e(TAG, "getObservable: ${e.message}")
                Observable.error(e.cause)
            } catch (e : ExecutionException) {
                Log.e(TAG, "getObservable: ${e.cause}", )
                Observable.error(e.cause)
            }
        }
    }

    fun getObservableFromCallable() = Observable.fromCallable(this::getData)

    fun getObservableFromFuture() = Observable.fromFuture(getFuture())
}
