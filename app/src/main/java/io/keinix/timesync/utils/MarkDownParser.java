package io.keinix.timesync.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import io.keinix.timesync.R;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableRenderer;
import ru.noties.markwon.spans.SpannableTheme;

public abstract class MarkDownParser {

    public static void parse(Context context, TextView textView, String text) {
        String parsedText = text.replace("&gt;", ">");
        Parser parser = Markwon.createParser();

            int blockQuoteColor = ContextCompat.getColor(context, R.color.colorAccent);
            SpannableTheme spannableTheme = SpannableTheme
                    .builderWithDefaults(context)
                    .blockQuoteColor(blockQuoteColor)
                    .build();
           SpannableConfiguration markDownConfig = SpannableConfiguration.create(context).builder(context).theme(spannableTheme).build();

        SpannableRenderer spannableRenderer = new SpannableRenderer();
        Node node = parser.parse(parsedText);
        CharSequence markDownText = spannableRenderer.render(markDownConfig, node);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Markwon.unscheduleDrawables(textView);
        Markwon.unscheduleTableRows(textView);
        textView.setText(markDownText);
    }
}
