package im.nfc.flutter_nfc_kit

import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.util.Log
import java.io.IOException

object ByteUtils {
    private const val HEX_CHARS = "0123456789ABCDEF"
    private val HEX_CHARS_ARRAY = HEX_CHARS.toCharArray()

    fun String.hexToBytes(): ByteArray {
        if (length % 2 == 1) throw IllegalArgumentException()

        val result = ByteArray(length / 2)

        val str = this.uppercase()
        for (i in 0 until length step 2) {
            val firstIndex = HEX_CHARS.indexOf(str[i])
            val secondIndex = HEX_CHARS.indexOf(str[i + 1])
            require(!(firstIndex == -1 || secondIndex == -1))
            val octet = (firstIndex shl 4) or secondIndex
            result[i shr 1] = octet.toByte()
        }

        return result
    }

    fun ByteArray.toHexString(): String {
        val result = StringBuffer()
        forEach {
            result.append(it.toHexString())
        }
        return result.toString()
    }

    fun Byte.toHexString(): String {
        val octet = this.toInt()
        val firstIndex = (octet and 0xF0) ushr 4
        val secondIndex = octet and 0x0F
        return "${HEX_CHARS_ARRAY[firstIndex]}${HEX_CHARS_ARRAY[secondIndex]}"
    }

    // MifareClassic
    fun mifareClassPrintEntireBlock(mifareClassic: MifareClassic, sectorIndex: Int): ArrayList<String> {
        val sectorAsHex = arrayListOf<String>()
        val firstBlock: Int = mifareClassic.sectorToBlock(sectorIndex)
        val lastBlock = firstBlock + 4
        for (i in firstBlock until lastBlock) {
            try {
                var blockBytes: ByteArray = mifareClassic.readBlock(i)
                if (blockBytes.size < 16) {
                    throw IOException()
                }
                if (blockBytes.size > 16) {
                    blockBytes = blockBytes.copyOf(16)
                }
                val hex = blockBytes.toHexString()
                sectorAsHex.add(hex)
            } catch (e: Exception) {
                print(e)
            }
        }
        return sectorAsHex
    }
    fun mifareUltralightPrintEntireBlock(mifareUltralight: MifareUltralight, pageOffset: Int): ArrayList<String> {
        val pageAsHex = arrayListOf<String>()
        try {
            var pageBytes = mifareUltralight.readPages(pageOffset)
            val hex = pageBytes.toHexString()
            pageAsHex.add(hex)
        } catch (ex: Exception) {
            print(ex)
        }
        return pageAsHex
    }
}
