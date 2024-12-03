import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

// Abstract Person Class
abstract class Person {
    private String name;
    private String id;

    public Person(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public abstract void displayInfo();
}

// Passenger Class
class Passenger extends Person {
    private List<Service> services;
    private double serviceCost;

    public Passenger(String name) {
        super(name);
        this.services = new ArrayList<>();
        this.serviceCost = 0.0;
    }

    public void addService(Service service) {
        services.add(service);
        serviceCost += service.getPrice();
    }

    public List<Service> getServices() {
        return services;
    }

    public double getServiceCost() {
        return serviceCost;
    }

    @Override
    public void displayInfo() {
        System.out.println("Passenger: " + getName() + ", ID: " + getId());
        System.out.println("Services: " + services);
        System.out.println("Service Cost: $" + serviceCost);
    }
}

// Abstract Service Class
abstract class Service {
    private String name;
    private double price;

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getDescription();

    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}

// Meal Service
class Meal extends Service {
    public Meal(String mealType, double price) {
        super(mealType + " Meal", price);
    }

    @Override
    public String getDescription() {
        return "A delicious " + getName() + " served in-flight.";
    }
}

// Wi-Fi Service
class WiFi extends Service {
    public WiFi() {
        super("Wi-Fi", 15.0);
    }

    @Override
    public String getDescription() {
        return "High-speed in-flight internet.";
    }
}

// Extra Baggage Service
class ExtraBaggage extends Service {
    public ExtraBaggage() {
        super("Extra Baggage", 30.0);
    }

    @Override
    public String getDescription() {
        return "Allows 10kg extra baggage.";
    }
}

// Flight Class
class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private int totalSeats;
    private int availableSeats;
    private double price;
    private String dateTime;
    private List<Passenger> passengers;
    private boolean[] seatAvailability;

    public Flight(String flightNumber, String origin, String destination, int totalSeats, double price,
            String dateTime) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.price = price;
        this.dateTime = dateTime;
        this.passengers = new ArrayList<>();
        this.seatAvailability = new boolean[totalSeats];
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public boolean[] getSeatAvailability() {
        return seatAvailability;
    }

    public boolean bookSeat(Passenger passenger, int seatIndex) {
        if (seatIndex < 0 || seatIndex >= totalSeats || seatAvailability[seatIndex]) {
            return false;
        }
        passengers.add(passenger);
        seatAvailability[seatIndex] = true;
        availableSeats--;
        return true;
    }
}

// Airline Class
class Airline {
    private String name;
    private List<Flight> flights;

    public Airline(String name) {
        this.name = name;
        this.flights = new ArrayList<>();
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public int getTotalPassengers() {
        return flights.stream().mapToInt(flight -> flight.getPassengers().size()).sum();
    }
}

// Payment Class
class Payment {
    private String method;
    private double amount;

    public Payment(String method, double amount) {
        this.method = method;
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public double getAmount() {
        return amount;
    }

    public boolean processPayment() {
        // Simulate payment processing
        return true; // Always succeed for this demo
    }
}

// GUI Class
public class AirlineManagementSystemGUI extends JFrame {
    private Airline airline;
    private DefaultTableModel flightsTableModel, passengersTableModel;
    private JPanel contentPanel;
    private JComboBox<String> flightSelectionCombo;
    private Passenger currentPassenger;
    private Flight currentFlight;

    // User Credentials Storage
    private Map<String, String> userCredentials;

    public AirlineManagementSystemGUI() {
        airline = new Airline("SkyHigh Airlines");
        userCredentials = new HashMap<>(); // Initialize user credentials storage
        setupUI();

        JPanel loginPanel = createLoginPanel();
        contentPanel.add(loginPanel, "Login");

        showPanel("Login");
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
    }

    private void cancelBookingAction(JTable passengersTable) {
        int selectedRow = passengersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to cancel their booking.");
            return;
        }

        // Retrieve passenger and flight details
        String passengerName = (String) passengersTableModel.getValueAt(selectedRow, 0);
        String flightNumber = (String) passengersTableModel.getValueAt(selectedRow, 1);
        int seatNumber = (int) passengersTableModel.getValueAt(selectedRow, 2) - 1; // Adjust for 0-indexing

        // Find the flight
        Flight flight = airline.getFlights().stream()
                .filter(f -> f.getFlightNumber().equals(flightNumber))
                .findFirst()
                .orElse(null);

        if (flight != null) {
            // Remove passenger from flight and update seat availability
            Passenger passengerToRemove = flight.getPassengers().stream()
                    .filter(p -> p.getName().equals(passengerName))
                    .findFirst()
                    .orElse(null);

            if (passengerToRemove != null) {
                flight.getPassengers().remove(passengerToRemove);
                flight.getSeatAvailability()[seatNumber] = false; // Mark seat as available
                flight.setAvailableSeats(flight.getAvailableSeats() + 1);

                // Update flights table
                for (int i = 0; i < flightsTableModel.getRowCount(); i++) {
                    if (flightsTableModel.getValueAt(i, 0).equals(flightNumber)) {
                        flightsTableModel.setValueAt(flight.getAvailableSeats(), i, 3);
                        break;
                    }
                }

                // Remove passenger from passengers table
                passengersTableModel.removeRow(selectedRow);

                JOptionPane.showMessageDialog(this, "Booking successfully canceled.");
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Passenger not found.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Flight not found.");
        }
    }

    private void refreshDashboard() {
        // Remove the old dashboard panel
        contentPanel.remove(contentPanel.getComponent(0));

        // Recreate the dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, "Dashboard");

        // Force the content panel to refresh and show the updated dashboard
        contentPanel.revalidate();
        contentPanel.repaint();

        // Show the updated dashboard
        showPanel("Dashboard");
    }

    private void setupUI() {
        setTitle("FlyAwayNow");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1, 10, 10));
        sidebar.setBackground(new Color(40, 44, 52));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));

        JButton dashboardButton = createSidebarButton("Dashboard");
        JButton flightsButton = createSidebarButton("Flights");
        JButton passengersButton = createSidebarButton("Passengers");
        JButton servicesButton = createSidebarButton("Services");
        JButton exitButton = createSidebarButton("Exit");

        sidebar.add(dashboardButton);
        sidebar.add(flightsButton);
        sidebar.add(passengersButton);
        sidebar.add(servicesButton);
        sidebar.add(exitButton);

        add(sidebar, BorderLayout.WEST);

        // Content Area
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        JPanel dashboardPanel = createDashboardPanel();
        JPanel flightsPanel = createFlightPanel();
        JPanel passengersPanel = createPassengerPanel();
        JPanel servicesPanel = createServicesPanel();
        JPanel signUpPanel = createSignUpPanel(); // Create sign-up panel

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(flightsPanel, "Flights");
        contentPanel.add(passengersPanel, "Passengers");
        contentPanel.add(servicesPanel, "Services");
        contentPanel.add(signUpPanel, "Sign Up"); // Add sign-up panel to content

        dashboardButton.addActionListener(e -> showPanel("Dashboard"));
        flightsButton.addActionListener(e -> showPanel("Flights"));
        passengersButton.addActionListener(e -> showPanel("Passengers"));
        servicesButton.addActionListener(e -> showPanel("Services"));
        exitButton.addActionListener(e -> System.exit(0));

        showPanel("Dashboard");
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 243, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("FlyAwayNow");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(65, 160, 245));
        loginButton.setForeground(Color.BLACK);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(65, 160, 245));
        signUpButton.setForeground(Color.BLACK);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        loginPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        loginPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        loginPanel.add(signUpButton, gbc); // Add Sign Up button

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                showPanel("Dashboard"); // Navigate to dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        });

        signUpButton.addActionListener(e -> showPanel("Sign Up")); // Navigate to sign-up panel

        return loginPanel;
    }

    private JPanel createSignUpPanel() {
        JPanel signUpPanel = new JPanel(new GridBagLayout());
        signUpPanel.setBackground(new Color(240, 243, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signUpPanel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(65, 160, 245));
        signUpButton.setForeground(Color.WHITE);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        signUpPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        signUpPanel.add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        signUpPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        signUpPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        signUpPanel.add(signUpButton, gbc);

        signUpButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password!");
                return;
            }

            if (userCredentials.containsKey(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists! Please choose another.");
                return;
            }

            userCredentials.put(username, password); // Store the new credentials
            JOptionPane.showMessageDialog(this, "Sign Up Successful! You can now log in.");
            showPanel("Login"); // Navigate back to login
        });

        return signUpPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 243, 250));

        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 1, 20, 20));
        statsPanel.setBackground(new Color(240, 243, 250));

        JLabel totalPassengersLabel = createStatLabel("Total Passengers", String.valueOf(airline.getTotalPassengers()));
        statsPanel.add(totalPassengersLabel);

        panel.add(statsPanel, BorderLayout.NORTH);

        // Info Panel for Users
        JTextArea infoArea = new JTextArea(5, 20);
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(250, 250, 250));
        infoArea.setBorder(BorderFactory.createTitledBorder("Tips for Booking"));
        infoArea.setText(
                "- Enter your name correctly for ticket booking.\n" +
                        "- Select your flight based on departure time and destination.\n" +
                        "- Add services like Wi-Fi or meals during booking.\n" +
                        "- Reserve your seats early to avoid unavailability.\n" +
                        "- Ensure payment details are ready for fast booking.\n");

        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFlightPanel() {
        JPanel flightsPanel = new JPanel(new BorderLayout());
        flightsTableModel = new DefaultTableModel(
                new String[] { "Flight Number", "Origin", "Destination", "Seats", "Price", "Date & Time", "Status" },
                0);
        JTable flightsTable = new JTable(flightsTableModel);
        flightsPanel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Flights"));
        formPanel.setBackground(new Color(245, 250, 255));

        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField dateTimeField = new JTextField("yyyy-MM-dd HH:mm:ss");
        JButton generateFlightsButton = new JButton("Generate Flights");

        formPanel.add(new JLabel("Origin:"));
        formPanel.add(originField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Date & Time:"));
        formPanel.add(dateTimeField);
        formPanel.add(new JLabel(""));
        formPanel.add(generateFlightsButton);

        flightsPanel.add(formPanel, BorderLayout.SOUTH);

        generateFlightsButton.addActionListener(e -> {
            String origin = originField.getText();
            String destination = destinationField.getText();
            String dateTime = dateTimeField.getText();

            if (origin.isEmpty() || destination.isEmpty() || dateTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter origin, destination, and date/time!");
                return;
            }

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setLenient(false);
                dateFormat.parse(dateTime);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use 'yyyy-MM-dd HH:mm:ss'");
                return;
            }

            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                String flightNumber = "F" + (1000 + random.nextInt(9000));
                int seats = 50 + random.nextInt(150);
                double price = 50 + random.nextInt(450);
                Flight flight = new Flight(flightNumber, origin, destination, seats, price, dateTime);
                airline.addFlight(flight);
                flightsTableModel.addRow(
                        new Object[] { flightNumber, origin, destination, seats, "$" + price, dateTime, "Scheduled" });
            }

            originField.setText("");
            destinationField.setText("");
            dateTimeField.setText("yyyy-MM-dd HH:mm:ss");
            updateFlightSelectionDropdown();
            JOptionPane.showMessageDialog(this, "5 flights generated successfully!");
        });

        return flightsPanel;
    }

    private JPanel createPassengerPanel() {
        JPanel passengersPanel = new JPanel(new BorderLayout());
        passengersTableModel = new DefaultTableModel(
                new String[] { "Name", "Flight", "Seat", "Services", "Service Cost" }, 0);
        JTable passengersTable = new JTable(passengersTableModel);
        passengersPanel.add(new JScrollPane(passengersTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // Adjusted to 6 rows for the new cancel button
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Passenger"));
        formPanel.setBackground(new Color(245, 250, 255));

        JTextField nameField = new JTextField();
        flightSelectionCombo = new JComboBox<>();
        JButton selectServicesButton = new JButton("Select Services");
        JButton cancelBookingButton = new JButton("Cancel Booking"); // Create cancel booking button

        cancelBookingButton.setBackground(new Color(255, 80, 80)); // Set styling
        cancelBookingButton.setForeground(Color.BLACK);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Select Flight:"));
        formPanel.add(flightSelectionCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(selectServicesButton);
        formPanel.add(new JLabel("")); // Empty label for spacing
        formPanel.add(cancelBookingButton); // Add the cancel button

        passengersPanel.add(formPanel, BorderLayout.SOUTH);

        selectServicesButton.addActionListener(e -> {
            String name = nameField.getText();
            String flightNumber = (String) flightSelectionCombo.getSelectedItem();

            if (flightNumber == null) {
                JOptionPane.showMessageDialog(this, "No flights available. Please add flights first.");
                return;
            }

            Flight flight = airline.getFlights().stream()
                    .filter(f -> f.getFlightNumber().equals(flightNumber))
                    .findFirst()
                    .orElse(null);

            if (flight == null) {
                JOptionPane.showMessageDialog(this, "Flight not found!");
                return;
            }

            if (flight.getAvailableSeats() <= 0) {
                JOptionPane.showMessageDialog(this, "No seats available!");
                return;
            }

            currentPassenger = new Passenger(name);
            currentFlight = flight;
            showPanel("Services");
        });

        cancelBookingButton.addActionListener(e -> cancelBookingAction(passengersTable)); // Add action listener

        return passengersPanel;
    }

    private JPanel createServicesPanel() {
        JPanel servicesPanel = new JPanel(new GridBagLayout());
        servicesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Section: Meal Options
        JLabel mealLabel = new JLabel("Select Meals:");
        mealLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JCheckBox vegetarianMeal = new JCheckBox("Vegetarian Meal ($20)");
        JCheckBox nonVegMeal = new JCheckBox("Non-Vegetarian Meal ($25)");
        JCheckBox kidsMeal = new JCheckBox("Kids Meal ($15)");

        JPanel mealPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mealPanel.setBorder(BorderFactory.createTitledBorder("Meal Options"));
        mealPanel.add(vegetarianMeal);
        mealPanel.add(nonVegMeal);
        mealPanel.add(kidsMeal);

        // Section: Wi-Fi Options
        JLabel wifiLabel = new JLabel("Wi-Fi Service:");
        wifiLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JCheckBox wifiCheckBox = new JCheckBox("Wi-Fi ($15)");

        JPanel wifiPanel = new JPanel(new GridLayout(1, 1));
        wifiPanel.setBorder(BorderFactory.createTitledBorder("Wi-Fi Service"));
        wifiPanel.add(wifiCheckBox);

        // Section: Extra Baggage Options
        JLabel baggageLabel = new JLabel("Extra Baggage:");
        baggageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JCheckBox baggageCheckBox = new JCheckBox("Extra Baggage ($30)");

        JPanel baggagePanel = new JPanel(new GridLayout(1, 1));
        baggagePanel.setBorder(BorderFactory.createTitledBorder("Extra Baggage"));
        baggagePanel.add(baggageCheckBox);

        // Add Payment and Confirm Button
        JButton addPaymentButton = new JButton("Proceed to Payment");
        addPaymentButton.setFont(new Font("Arial", Font.BOLD, 14));
        addPaymentButton.setBackground(new Color(65, 160, 245));
        addPaymentButton.setForeground(Color.BLACK);

        // Add Components to GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        servicesPanel.add(mealLabel, gbc);

        gbc.gridy++;
        servicesPanel.add(mealPanel, gbc);

        gbc.gridy++;
        servicesPanel.add(wifiLabel, gbc);

        gbc.gridy++;
        servicesPanel.add(wifiPanel, gbc);

        gbc.gridy++;
        servicesPanel.add(baggageLabel, gbc);

        gbc.gridy++;
        servicesPanel.add(baggagePanel, gbc);

        gbc.gridy++;
        servicesPanel.add(addPaymentButton, gbc);

        // Button Action to Proceed to Payment
        addPaymentButton.addActionListener(e -> {
            if (currentPassenger == null || currentFlight == null) {
                JOptionPane.showMessageDialog(this, "No passenger or flight selected.");
                return;
            }

            // Calculate fresh service costs based on selected checkboxes
            double serviceCost = 0.0;
            if (vegetarianMeal.isSelected())
                serviceCost += 20;
            if (nonVegMeal.isSelected())
                serviceCost += 25;
            if (kidsMeal.isSelected())
                serviceCost += 15;
            if (wifiCheckBox.isSelected())
                serviceCost += 15;
            if (baggageCheckBox.isSelected())
                serviceCost += 30;

            // Add selected services to the passenger
            currentPassenger.getServices().clear(); // Reset services to avoid duplicates
            if (vegetarianMeal.isSelected())
                currentPassenger.addService(new Meal("Vegetarian", 20));
            if (nonVegMeal.isSelected())
                currentPassenger.addService(new Meal("Non-Vegetarian", 25));
            if (kidsMeal.isSelected())
                currentPassenger.addService(new Meal("Kids", 15));
            if (wifiCheckBox.isSelected())
                currentPassenger.addService(new WiFi());
            if (baggageCheckBox.isSelected())
                currentPassenger.addService(new ExtraBaggage());

            // Calculate total cost
            double totalCost = currentFlight.getPrice() + serviceCost;

            // Show payment dialog with the calculated total cost
            showPaymentDialog(totalCost);
        });

        return servicesPanel;
    }

    private void showPaymentDialog(double totalCost) {
        JDialog paymentDialog = new JDialog(this, "Payment", true);
        paymentDialog.setSize(400, 300);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel paymentMethodLabel = new JLabel("Select Payment Method:");
        JComboBox<String> paymentMethodCombo = new JComboBox<>(
                new String[] { "Credit Card", "PayPal", "Bank Transfer" });

        JLabel totalCostLabel = new JLabel("Total Cost: $" + totalCost); // Display correct cost dynamically
        JButton confirmPaymentButton = new JButton("Confirm Payment");

        paymentDialog.add(paymentMethodLabel);
        paymentDialog.add(paymentMethodCombo);
        paymentDialog.add(totalCostLabel);
        paymentDialog.add(confirmPaymentButton);

        confirmPaymentButton.addActionListener(e -> {
            String selectedMethod = (String) paymentMethodCombo.getSelectedItem();
            if (selectedMethod == null) {
                JOptionPane.showMessageDialog(this, "Please select a payment method.");
                return;
            }

            Payment payment = new Payment(selectedMethod, totalCost);
            if (!payment.processPayment()) {
                JOptionPane.showMessageDialog(this, "Payment failed! Try again.");
                return;
            }

            // Show Seat Selection
            int selectedSeat = showSeatSelectionDialog(currentFlight.getSeatAvailability());
            if (selectedSeat == -1 || !currentFlight.bookSeat(currentPassenger, selectedSeat)) {
                JOptionPane.showMessageDialog(this, "Failed to book seat!");
                return;
            }

            // Add Passenger to Passenger Table
            passengersTableModel.addRow(new Object[] {
                    currentPassenger.getName(),
                    currentFlight.getFlightNumber(),
                    selectedSeat + 1,
                    currentPassenger.getServices().toString(),
                    "$" + currentPassenger.getServiceCost()

            });

            // Update Flight Seat Count in Flights Table
            for (int i = 0; i < flightsTableModel.getRowCount(); i++) {
                if (flightsTableModel.getValueAt(i, 0).equals(currentFlight.getFlightNumber())) {
                    flightsTableModel.setValueAt(currentFlight.getAvailableSeats(), i, 3);
                    break;
                }
            }

            // Reset
            currentPassenger = null;
            currentFlight = null;
            paymentDialog.dispose();
            showPanel("Passengers");
            refreshDashboard();
        });

        paymentDialog.setVisible(true);
    }

    private int showSeatSelectionDialog(boolean[] seatAvailability) {
        // Create a dialog for seat selection
        JDialog seatDialog = new JDialog(this, "Select Seat", true);
        seatDialog.setSize(900, 600);
        seatDialog.setLocationRelativeTo(this);
        seatDialog.setLayout(new BorderLayout());

        // Panel to hold the seat map
        JPanel seatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints seatGbc = new GridBagConstraints();
        seatGbc.insets = new Insets(5, 5, 5, 5); // Padding between seats

        // Airplane configuration: Adjust dynamically to match seatAvailability
        char[] columns = { 'A', 'B', 'C', 'D', 'E', 'F' };
        int cols = 6; // 6 seats per row (A-F)
        int totalSeats = seatAvailability.length;
        int rows = (int) Math.ceil((double) totalSeats / cols); // Calculate rows based on total seats
        JButton[][] seatButtons = new JButton[rows][cols];
        final int[] selectedSeat = { -1 };

        // Create seats
        for (int row = 0; row < rows; row++) {
            // Add row label
            seatGbc.gridx = 0; // First column for row labels
            seatGbc.gridy = row + 1;
            JLabel rowLabel = new JLabel("Row " + (row + 1));
            seatPanel.add(rowLabel, seatGbc);

            for (int col = 0; col < cols; col++) {
                // Calculate the seat index
                int seatIndex = row * cols + col;

                // Stop if we've reached the total number of seats
                if (seatIndex >= totalSeats)
                    break;

                seatGbc.gridx = col + 1 + (col >= 3 ? 1 : 0); // Add aisle spacing
                seatGbc.gridy = row + 1;

                // Create seat button
                seatButtons[row][col] = new JButton(seatAvailability[seatIndex] ? "Booked" : columns[col] + "");
                seatButtons[row][col].setEnabled(!seatAvailability[seatIndex]);
                seatButtons[row][col].setPreferredSize(new Dimension(50, 50)); // Seat size

                // Color coding for seats
                // Color coding for seats
                if (row == 11 || row == 12) { // Emergency exit rows
                    seatButtons[row][col].setBackground(Color.CYAN); // Highlight exit rows
                } else if (!seatAvailability[seatIndex]) {
                    seatButtons[row][col].setBackground(Color.LIGHT_GRAY); // Available seat
                } else {
                    seatButtons[row][col].setBackground(Color.RED); // Booked seat
                }

                int finalSeatIndex = seatIndex;
                seatButtons[row][col].addActionListener(e -> {
                    selectedSeat[0] = finalSeatIndex;
                    seatDialog.dispose();
                });

                // Add seat button to the panel
                seatPanel.add(seatButtons[row][col], seatGbc);
            }
        }

        // Add column labels at the top
        for (int col = 0; col < cols; col++) {
            seatGbc.gridx = col + 1 + (col >= 3 ? 1 : 0); // Adjust for aisle
            seatGbc.gridy = 0; // Top row for column labels
            JLabel colLabel = new JLabel(String.valueOf(columns[col]));
            seatPanel.add(colLabel, seatGbc);
        }

        // Wrap seatPanel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add the scroll pane to the dialog
        seatDialog.add(scrollPane, BorderLayout.CENTER);

        // Show the dialog
        seatDialog.setVisible(true);
        return selectedSeat[0];
    }

    private void updateFlightSelectionDropdown() {
        flightSelectionCombo.removeAllItems();
        airline.getFlights().forEach(flight -> flightSelectionCombo.addItem(flight.getFlightNumber()));
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel("<html><div style='font-size:18px;font-weight:bold;'>" + title + "</div>" +
                "<div style='font-size:24px;color:#007BFF;'>" + value + "</div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        label.setBackground(Color.WHITE);
        label.setOpaque(true);
        return label;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 55, 60));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 80, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 55, 60));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AirlineManagementSystemGUI gui = new AirlineManagementSystemGUI();
            gui.setVisible(true);
        });
    }
}