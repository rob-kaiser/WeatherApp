package com.bwar.WeatherApp.view;

import com.bwar.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


@SpringUI(path = "")
public class MainView extends UI {
    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String>unitSelect;
    private TextField cityTextField;
    private Button showWeatherButton;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label sunRiseLabel;
    private Label sunSetLabel;
    private ExternalResource img;
    private Image iconImage;
    private HorizontalLayout dashboardMain;
    private HorizontalLayout mainDescriptionLayout;
    private VerticalLayout descriptionLayout;
    private VerticalLayout pressureLayout;


    @Override
    protected void init(VaadinRequest request) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashboardTitle();
        dashboardDescription();

        showWeatherButton.addClickListener(clickEvent -> {
            if (!cityTextField.getValue().equals("") ) {
                try {
                    updateUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else Notification.show("Please enter a city");

        });

    }

    private void setUpForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("40px");
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(1));

        formLayout.addComponents(unitSelect);

        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponents(cityTextField);

        showWeatherButton = new Button();
        showWeatherButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(showWeatherButton);

        mainLayout.addComponents(formLayout);

    }


    public void setUpLayout(){

        iconImage = new Image();
        weatherDescription = new Label("Description: Clear Skies");
        weatherMin = new Label("Min: 56F");
        weatherMax = new Label("Max: 70F");
        pressureLabel = new Label("Pressure: 123pa");
        humidityLabel = new Label("Humidity: 34");
        windSpeedLabel = new Label("Wind Speed: 32mph");
        sunRiseLabel = new Label("Sunrise: 7:00AM");
        sunSetLabel = new Label("Sunset: 7:00PM");


        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLayout);
    }
    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Rob's Weather App!");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        headerLayout.addComponents(title);

        mainLayout.addComponents(headerLayout);
    }

    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Image icon = new Image(null, new ClassResource("/weather_icon.jpg"));
        icon.setWidth("125px");
        icon.setHeight("125px");

        logoLayout.addComponents(icon);

        mainLayout.addComponents(logoLayout);

    }

    private void dashboardTitle() {
        dashboardMain = new HorizontalLayout();
        dashboardMain.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        currentLocationTitle = new Label("Currently in Spokane");
        currentLocationTitle.addStyleName(ValoTheme.LABEL_H2);
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LIGHT);

        currentTemp = new Label("19F");
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);

    }

    private void dashboardDescription() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        descriptionLayout = new VerticalLayout();
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        descriptionLayout.addComponents(weatherDescription);
        descriptionLayout.addComponents(weatherMin);
        descriptionLayout.addComponents(weatherMax);


        pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        pressureLayout.addComponents(pressureLabel);
        pressureLayout.addComponents(humidityLabel);
        pressureLayout.addComponents(windSpeedLabel);
        pressureLayout.addComponents(sunRiseLabel);
        pressureLayout.addComponents(sunSetLabel);

    }

    private void updateUI() throws JSONException {

        String city = cityTextField.getValue();
        String defaultUnit;
        String unit;

        if (unitSelect.getValue().equals("F")) {
            defaultUnit = "imperial";
            unitSelect.setValue("F");
            unit = "\u00b0"+"F";
        } else {
            defaultUnit = "metric";
            unitSelect.setValue("C");
            unit = "\u00b0"+"C";
        }

        weatherService.setCityName(city);
        weatherService.setUnit(defaultUnit);


        currentLocationTitle.setValue("Currently in " + city);

        JSONObject myObject = weatherService.returnMainObject();
        double temp = myObject.getDouble("temp");

        currentTemp.setValue(temp+unit);

        JSONObject mainObject = weatherService.returnMainObject();
        double minTemp = mainObject.getDouble("temp_min");
        double maxTemp = mainObject.getDouble("temp_max");
        int pressure = mainObject.getInt("pressure");
        int humidity = mainObject.getInt("humidity");

        JSONObject windObject = weatherService.returnWindObject();
        double wind = windObject.getDouble("speed");

        JSONObject systemObject = weatherService.returnSunSet();
        long sunRise = systemObject.getLong("sunrise")*1000;
        long sunSet = systemObject.getLong("sunset")*1000;


        String iconCode = null;
        String description ="";

        JSONArray jsonArray = weatherService.returnWeatherArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            description = weatherObject.getString("description");
            iconCode = weatherObject.getString("icon");
            System.out.println(iconCode);
        }


        iconImage.setSource(new ExternalResource("http://openweathermap.org/img/w/"+iconCode+".png"));

        dashboardMain.addComponents(currentLocationTitle, iconImage, currentTemp);
        mainLayout.addComponents(dashboardMain);

        weatherDescription.setValue("Conditions: " + description);
        weatherMin.setValue("Min: " + minTemp + unit);
        weatherMax.setValue("Max: " + maxTemp + unit);
        pressureLabel.setValue("Pressure: " + pressure + " hpa");
        humidityLabel.setValue("Humidity: " + humidity + " %");

        windSpeedLabel.setValue("Wind: " + wind + " mph");
        sunRiseLabel.setValue("Sunrise: " + convertTime(sunRise));
        sunSetLabel.setValue("Sunset: " + convertTime(sunSet));

        mainDescriptionLayout.addComponents(descriptionLayout, pressureLayout);
        mainLayout.addComponents(mainDescriptionLayout);



    }

    private String convertTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy hh:mmaa");

                return dateFormat.format(new Date(time));
    }

}
