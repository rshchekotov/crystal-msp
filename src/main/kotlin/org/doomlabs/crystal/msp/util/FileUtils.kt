package org.doomlabs.crystal.msp.util

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.*

@Throws(IOException::class)
fun unzip(zipFile: File, destDir: File) {
    ZipArchiveInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
        var entry: ZipArchiveEntry?
        while (zis.nextZipEntry.also { entry = it } != null) {
            val entryFile = File(destDir, entry!!.name)
            if (entry!!.isDirectory) {
                entryFile.mkdirs()
            } else {
                val parent = entryFile.parentFile
                if (!parent.exists()) parent.mkdirs()
                entryFile.outputStream().use { fos -> zis.copyTo(fos) }
            }
        }
    }
}