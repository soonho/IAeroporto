/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes.mailer;

import java.io.Serializable;

/**
 *
 * @author soonho
 */
public class Message implements Serializable {
    private String title;
    private String content;
    private String destiny;

    public Message(String title, String content, String destiny) {
        this.title = title;
        this.content = content;
        this.destiny = destiny;
    }

    @Override
    public String toString() {
        return "Message{" + "title=" + title + ", content=" + content + ", destiny=" + destiny + '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
