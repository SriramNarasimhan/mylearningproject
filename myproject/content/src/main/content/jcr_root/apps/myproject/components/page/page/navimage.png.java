/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */

package libs.foundation.components.page;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.jcr.RepositoryException;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.commons.AbstractImageServlet;
import com.day.image.Font;
import com.day.image.Layer;

/**
 * Renders the navigation image
 */
public class navimage_png extends AbstractImageServlet {


    protected Layer createLayer(ImageContext ctx)
            throws RepositoryException, IOException {
        PageManager pageManager = ctx.resolver.adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(ctx.resource);

        // constants
        int scale = 6;
        int paddingX = 24;
        int paddingY = 24;
        Color bgColor = Color.WHITE;

        String title = currentPage.getTitle();
        if (title == null) {
            title = currentPage.getName();
        }
        title = title.toUpperCase();
        Paint titleColor = Color.BLACK;
        Font titleFont = new Font("Myriad Pro", 10 * scale, Font.BOLD);
        int titleBase = 10 * scale;

        String subtitle = currentPage.getProperties().get("subtitle", "");
        Paint subtitleColor = Color.BLACK;
        Font subTitleFont = new Font("Tahoma", 7);
        int subTitleBase = 20;

        // draw the title text (4 times bigger)
        Rectangle2D titleExtent = titleFont.getTextExtent(0, 0, 0, 0, title, Font.ALIGN_LEFT, 0, 0);
        Rectangle2D subtitleExtent = subTitleFont.getTextExtent(0, 0, 0, 0, subtitle, Font.ALIGN_LEFT, 0, 0);
        // check if subtitleExtent is too width
        if ( subtitle.length() > 0 ) {
            int titleWidth = (int)titleExtent.getWidth() / scale;
            if ( subtitleExtent.getWidth() > titleWidth && subtitleExtent.getWidth() + 2 * paddingX > 150 ) {
                int charWidth = (int)subtitleExtent.getWidth() / subtitle.length();
                int maxWidth = (150 > titleWidth + 2  * paddingX ? 150 - 2 * paddingX : titleWidth);
                int len = (maxWidth - ( 2 * charWidth) ) / charWidth;
                subtitle = subtitle.substring(0, len) + "...";
                subtitleExtent = subTitleFont.getTextExtent(0, 0, 0, 0, subtitle, Font.ALIGN_LEFT, 0, 0);
            }
        }
        int width = Math.max((int) titleExtent.getWidth(), (int) subtitleExtent.getWidth());

        Layer text = new Layer(width, (int) titleExtent.getHeight() + 40, new Color(0x01ffffff, true));
        text.setPaint(titleColor);
        text.drawText(0, titleBase, 0, 0, title, titleFont, Font.ALIGN_LEFT | Font.ALIGN_BASE, 0, 0);
        text.resize(text.getWidth() / scale, text.getHeight() / scale);
        text.setX(0);
        text.setY(0);

        if (subtitle.length() > 0) {
            // draw the subtitle normal sized
            text.setPaint(subtitleColor);
            text.drawText(0, subTitleBase, 0, 0, subtitle, subTitleFont, Font.ALIGN_LEFT | Font.ALIGN_BASE, 0, 0);
        }

        // and merge the layers
        text.setY(paddingY);
        text.setX(paddingX);
        text.setBackgroundColor(bgColor);
        int bgWidth = 150;
        if ( text.getWidth() + 2 * paddingX > bgWidth ) {
            bgWidth = text.getWidth() + 2 * paddingX;
        }
        Layer bg = new Layer(bgWidth, 100, bgColor);
        bg.merge(text);

        return bg;
    }
}
