package com.getstream.sdk.chat.style

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import io.getstream.chat.android.client.logger.ChatLogger
import java.util.HashMap

public class ChatFontsImpl(
    private val style: ChatStyle,
    private val context: Context
) : ChatFonts {

    private val resourceMap: MutableMap<Int, Typeface> = HashMap()
    private val pathMap: MutableMap<String, Typeface> = HashMap()

    private val logger = ChatLogger.get(ChatFonts::class.java.simpleName)

    override fun setFont(textStyle: TextStyle, textView: TextView) {
        if (textStyle.font != null) {
            textView.setTypeface(textStyle.font, textStyle.style)
        } else {
            setDefaultFont(textView, textStyle.style)
        }
    }

    override fun getFont(textStyle: TextStyle): Typeface? {
        return when {
            textStyle.fontResource != -1 ->
                getFont(textStyle.fontResource)
            !textStyle.fontAssetsPath.isNullOrEmpty() ->
                getFont(textStyle.fontAssetsPath!!)
            else -> null
        }
    }

    private fun getFont(fontPath: String): Typeface? {
        if (fontPath in pathMap) {
            return pathMap[fontPath]
        }

        val typeface = safeLoadTypeface(fontPath) ?: return null
        pathMap[fontPath] = typeface
        return typeface
    }

    private fun getFont(@FontRes fontRes: Int): Typeface? {
        if (fontRes in resourceMap) {
            return resourceMap[fontRes]
        }

        val typeface = safeLoadTypeface(fontRes) ?: return null
        resourceMap.put(fontRes, typeface)
        return typeface
    }

    private fun setDefaultFont(textView: TextView, textStyle: Int) {
        if (style.hasDefaultFont()) {
            textView.setTypeface(getFont(style.getDefaultTextStyle()), textStyle)
        } else {
            textView.setTypeface(Typeface.DEFAULT, textStyle)
        }
    }

    private fun safeLoadTypeface(@FontRes fontRes: Int): Typeface? {
        return try {
            ResourcesCompat.getFont(context, fontRes)
        } catch (t: Throwable) {
            logger.logE(t)
            null
        }
    }

    private fun safeLoadTypeface(fontPath: String): Typeface? {
        return try {
            Typeface.createFromAsset(context.assets, fontPath)
        } catch (t: Throwable) {
            logger.logE(t)
            null
        }
    }
}
