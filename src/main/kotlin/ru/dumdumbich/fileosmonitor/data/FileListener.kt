package ru.dumdumbich.fileosmonitor.data
import org.apache.commons.io.monitor.FileAlterationListener
import org.apache.commons.io.monitor.FileAlterationObserver
import java.io.File
import java.time.Instant


class FileListener : FileAlterationListener {

    override fun onStart(observer: FileAlterationObserver?) {
//        println("FileListener : onStart")
    }

    override fun onDirectoryCreate(directory: File?) {
        println("FileListener : onDirectoryCreate [ $directory : ${Instant.ofEpochMilli(directory?.lastModified() ?: 0)} ]")
    }

    override fun onDirectoryChange(directory: File?) {
        println("FileListener : onDirectoryChange [ $directory : ${Instant.ofEpochMilli(directory?.lastModified() ?: 0)} ]")
    }

    override fun onDirectoryDelete(directory: File?) {
        println("FileListener : onDirectoryDelete [ $directory : ${Instant.now()} ]")
    }

    override fun onFileCreate(file: File?) {
        println("FileListener : onFileCreate [ $file : ${Instant.ofEpochMilli(file?.lastModified() ?: 0)} ]")
    }

    override fun onFileChange(file: File?) {
        println("FileListener : onFileChange [ $file : ${Instant.ofEpochMilli(file?.lastModified() ?: 0)} ]")
    }

    override fun onFileDelete(file: File?) {
        println("FileListener : onFileDelete [ $file : ${Instant.now()} ]")
    }

    override fun onStop(observer: FileAlterationObserver?) {
//        println("FileListener : onStop")
    }

}
