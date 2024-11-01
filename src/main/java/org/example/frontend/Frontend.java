package org.example.frontend;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Route("")
@PageTitle("Start")
public class Frontend extends VerticalLayout implements KeyNotifier {
    private final TextField input = new TextField("", "Type here...");
    private final Button inputBtn = new Button("Save", VaadinIcon.CHECK.create());
    private final Button viewBtn = new Button("Show");
    private TextArea textArea = new TextArea();

    public Frontend() {
        HorizontalLayout upper_layout = new HorizontalLayout();
        HorizontalLayout lower_layout = new HorizontalLayout();

        inputBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        inputBtn.addClickListener(e -> {
            sendMessageToServer(input.getValue());
            input.setValue("");
        });
        viewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        viewBtn.addClickListener(e -> {
            textArea.setValue(getMessageFromServer());
            input.setValue("");
        });

        input.setValueChangeMode(ValueChangeMode.EAGER);
        input.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().trim().isEmpty()) {
                inputBtn.setEnabled(false);
            }
            else {
                inputBtn.setEnabled(true);
                input.addKeyDownListener(Key.ENTER, ev -> inputBtn.click());
            }
        });
        inputBtn.setEnabled(false);

        textArea.setWidth("300px");
        textArea.setLabel("Output");
        textArea.setValue("");
        textArea.setReadOnly(true);

        upper_layout.add(input, inputBtn, viewBtn);
        lower_layout.add(textArea);

        add(upper_layout, lower_layout);
        setHeight("100%");
        getElement().setAttribute("theme", Lumo.DARK);
    }

    private void sendMessageToServer(String message) {
        try (Socket socket = new Socket("172.28.0.2", 12345)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(message);

            String serverResponse = in.readLine();
            Notification.show("Server response: " + serverResponse);

        } catch (IOException ex) {
            ex.printStackTrace();
            Notification.show("Error: " + ex.getMessage());
        }
    }

    
    private String getMessageFromServer() {
        try (Socket socket = new Socket("172.28.0.2", 12345)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("&&&ttt&&&");

            String temp;
            StringBuilder serverResponse = new StringBuilder();
            while ((temp = in.readLine()) != null && !temp.isEmpty()) {
                serverResponse.append(temp).append(System.lineSeparator());
            }
            return serverResponse.toString().trim();

        } catch (IOException ex) {
            ex.printStackTrace();
            Notification.show("Error: " + ex.getMessage());
        }

        return "";
    }
}

