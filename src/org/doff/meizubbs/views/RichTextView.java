package org.doff.meizubbs.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

public class RichTextView extends android.widget.TextView {
	public RichTextView(Context context,AttributeSet attr) {
		super(context,attr);
		// TODO Auto-generated constructor stub
		this.setMovementMethod(LinkMovementMethod.getInstance()); 
	}

	@Override
	public void setText(CharSequence text, BufferType type) {

		String cs = text.toString();


		Spanned span = Html.fromHtml(cs, new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				
				Drawable drawable = null;
				String sourceName = getContext().getPackageName()
						+ ":drawable/" + source;
				int id = getResources().getIdentifier(sourceName, null, null);
				if (id != 0) {
					drawable = getResources().getDrawable(id);
					if (drawable != null) {
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
								drawable.getIntrinsicHeight());
					}
				}
				return drawable;
			}
		}, null);
	
		super.setText(span, type);
	}

}
