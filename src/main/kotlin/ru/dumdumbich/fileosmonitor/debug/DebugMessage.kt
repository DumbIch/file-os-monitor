package ru.dumdumbich.fileosmonitor.debug

/**
 * <h3>file-os-monitor</h3>
 * @description
 * <p>Debug message service</p>
 * @author DumbIch
 * @date 2023-10-04 09:14
 **/

/**
 * Класс <code>DebugMessage</code> предназначен для организации вывода информации в консоль или в лог-файл
 * исключительно для обеспечения возможности отладки приложения в процессе разработки.
 * Для формирования журнала работы приложения в процессе эксплуатации применяется служба <code>LogService</code>
 *
 * Для отображения имени метода в отладочном сообщении необходимо в тело соответствующего метода
 * поместить строку <code>consoleMsg(object {}.javaClass.enclosingMethod.name)</code>
 *
 * ВНИМАНИЕ:
 * Путь к лог-файлу жестко привязан к корневой папке лог-файлов. Имя папки определяется именем пакета приложения.
 * Путь задан жестко, т.к. этот лог-файл формируется и используется исключительно во время разработки.

 *
 * ОПИСАНИЕ РАБОТЫ
 * + проверяет наличие директории с именем проекта в папке log
 * + если ее нет - создает папку
 * + проверяет наличие лог-файла с именем log.txt в указанной папке
 * + если его нет - создает файл
 * + если папка и файл существуют - дописывает логи в существующий файл
 * + если лог-файл превышает установленный предельный размер - выводится сообщение на экран,
 *   а также производится архивирование лог-файла, изменив расширение с xxx.log на xxx.nnn - где nnn - номер лог-файла.
 *
 * @author DumDumbIch (dumdumbich@mail.ru)
 * @version 1.0
 * @date 10.03.2023
 */

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


interface DebugMessage {

    companion object {
        const val DEBUG_Server: Boolean = true
        private const val ENTRY_PART_EMPTY = ""
        private const val ENTRY_PART_UNIQUE = "@@@"
        private const val LOG_DIRECTORY_ROOT = "/home/dumdumbich/develop/log/"
        private const val ENTRIES_MAX_NUMBER = 10L
        private const val ENTRIES_MAX_LENGTH = 80L
        private const val LOG_FILE_SIZE_MAX = ENTRIES_MAX_NUMBER * ENTRIES_MAX_LENGTH
        private const val LOF_FILE_BACKUP_NUMBER_MAX = 5
    }

    private val appPackageName: String
        get() = this.javaClass.canonicalName.substringBefore(".")

    private val logDirectoryPath: String
        get() = "$LOG_DIRECTORY_ROOT$appPackageName"

    private val logFilePath: String
        get() = "$logDirectoryPath/$appPackageName.log"

    private val backupFilePath: String
        get() = "$logDirectoryPath/$appPackageName"

    private val logFileSize: Long
        get() = getLogFileSize(stringToPath(logFilePath))

    val className: String
        get() = this.javaClass.simpleName

    private val classHash: String
        get() = this.hashCode().toString()

    private val currentThread: String
        get() = Thread.currentThread().name


    fun consoleMsg(message: String, isDebugMode: Boolean = true) {
        if (isDebugMode) println("$ENTRY_PART_UNIQUE ${timestamp()} [$currentThread] $className[$classHash]: $message")
    }

    fun fileMsg(message: String, isDebugMode: Boolean = true) {
        if (isDebugMode) {
            val directory: Path = stringToPath(logDirectoryPath)
            val file: Path = stringToPath(logFilePath)
            if (isPathNotExists(directory)) createDirectory(directory)
            if (isPathNotExists(file)) createFile(file)
            if (logFileSize >= LOG_FILE_SIZE_MAX) {
                consoleMsg("WARNING: File size is $logFileSize exceeds the set limit of $LOG_FILE_SIZE_MAX")
                if (!backupLogFile()) {
                    consoleMsg("ERROR: Failed to create backup file")
                }
            } else {
                addTextToFile(
                    "$ENTRY_PART_UNIQUE : ${timestamp()} : $classHash : $className : $message \n", file
                )
            }
        }
    }

    fun consoleMsgCfg(
        message: String,
        isDebugMode: Boolean = true,
        onUnique: Boolean = true,
        onTimestamp: Boolean = true,
        onThread: Boolean = true,
        onClass: Boolean = true,
        onHash: Boolean = true
    ) {
        if (isDebugMode) {
            val unique = if (onUnique) ENTRY_PART_UNIQUE else ENTRY_PART_EMPTY
            val timestamp = if (onTimestamp) timestamp() else ENTRY_PART_EMPTY
            val thread = if (onThread) "[$currentThread]" else ENTRY_PART_EMPTY
            val klass = if (onClass) className else ENTRY_PART_EMPTY
            val hash = if (onHash) "[$classHash]" else ENTRY_PART_EMPTY
            println("$unique $timestamp $thread $klass$hash: $message")
        }
    }

    /**
     * Формирование временной метки для конкретной записи
     */
    private fun now(): LocalDateTime = LocalDateTime.now()

    private fun timestampByTemplate(template: String): String = now()
        .format(DateTimeFormatter.ofPattern(template))

    private fun timestamp(): String = timestampByTemplate("dd/MM/yyyy HH:mm:ss A")


    /**
     * Добавление записи в лог-файл
     */
    private fun stringToPath(path: String): Path = Paths.get(path)

    private fun isPathExists(path: Path): Boolean = Files.exists(path)
    private fun isPathNotExists(path: Path): Boolean = !isPathExists(path)

    private fun addTextToFile(data: String, path: Path): Boolean =
        try {
            Files.write(
                path,
                data.toByteArray(),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
            )
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    private fun getLogFileSize(path: Path): Long = try {
        Files.size(path)
    } catch (e: IOException) {
        e.printStackTrace()
        -1
    }

    private fun createDirectory(path: Path): Boolean = try {
        Files.createDirectory(path)
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }

    private fun createFile(path: Path): Boolean = try {
        Files.createFile(path)
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }

    private fun renameFile(fromPath: Path, toPath: Path): Boolean = try {
        Files.move(fromPath, toPath)
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }

    /**
     * Архивирование лог-файла, изменив расширение с xxx.log на xxx.nnn - где nnn - номер лог-файла.
     * После этого создается новый файл xxx.log и запись продолжается.
     * Процесс повторяется пока не будет создано максимально установленное количество архивных файлов.
     */
    private fun backupLogFile(): Boolean = try {
        var result = false
        val fromPath = stringToPath(logFilePath)
        if (isPathExists(fromPath)) {
            for (number in 0..LOF_FILE_BACKUP_NUMBER_MAX) {
                val toPath = stringToPath("$backupFilePath.$number")
                if (isPathNotExists(toPath)) {
                    if (renameFile(fromPath, toPath)) {
                        result = true
                        consoleMsg("Backup file $backupFilePath.$number successfully created")
                        break
                    }
                }
            }
        }
        result
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }

}
