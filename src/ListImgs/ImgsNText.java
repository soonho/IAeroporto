/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ListImgs;

import javax.swing.Icon;

/**
 *
 * @author mhayk
 */
public class ImgsNText {

    private String name;
    private Icon img;

    public ImgsNText(String text, Icon icon) {
        this.name = text;
        this.img = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Icon getImg() {
        return img;
    }

    public void setImg(Icon img) {
        this.img = img;
    }

}
