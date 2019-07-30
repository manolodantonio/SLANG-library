package com.manzo.slang.extensions

import android.content.Context
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */


/**
 * Zips the file to an archive - creates it if necessary
 * @receiver List<File>
 * @param context Context
 * @param zipFilename String
 */
fun File.zip(context: Context, zipFilename: String = "archive.zip") =
    context.getInternalFile(zipFilename).also { addToZip(it) }

/**
 * Returns a zipped archive file
 * @receiver List<File>
 * @param context Context
 * @param zipFilename String
 */
fun List<File>.zip(context: Context, zipFilename: String = "archive.zip") =
    context.getInternalFile(zipFilename).also { addToZip(it) }


/**
 *  Add file to a target zip file.
 * @receiver File
 * @param zipFile File
 */
fun File.addToZip(zipFile: File) = listOf(this).addToZip(zipFile)

/**
 * Add files to a target zip file.
 * @receiver List<File>
 * @param zipFile File
 */
fun List<File>.addToZip(zipFile: File) =
    ZipOutputStream(zipFile.outputStream().buffered()).use { output ->
        forEach { addFile ->
            addFile.inputStream().buffered().use { origin ->
                output.putNextEntry(ZipEntry(addFile.name))
                origin.copyTo(output, 1024)
            }
        }
    }


/**
 * Searches the file and returns the first line containing the query, or empty.
 * @param query String
 * @return String
 */
fun File.findLine(query: String) =
    bufferedReader().readLines().firstOrNull { it.contains(query) } ?: ""


/**
 * Searches the file and returns the first line containing the query, or null.
 * @param query String
 * @return String
 */
fun File.findLineOrNull(query: String) =
    bufferedReader().readLines().firstOrNull { it.contains(query) }
