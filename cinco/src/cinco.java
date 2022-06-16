

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;


import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

import java.util.*;

import cinco.Severity;
import cinco.StaffMember;
import cinco.TicketStatus;
import cinco.TimeSpan;
import cinco.technician;
import cinco.ticket;
import cinco.authentication.AuthenticationResult;
import cinco.authentication.Authenticator;
import cinco.authentication.Group;
import cinco.authentication.InvalidUserNameOrPasswordException;
import cinco.authentication.User;

public class cinco {
	// object lists for employee's and technicians
	private static List<User> listOfUsers = new ArrayList<User>(); // JC: Only need one list of type User as concrete
																	// instances identify themselves

	// list for IT tickets in the system
	private static List<ticket> listOfTickets = new ArrayList<ticket>();

	public static void main(String[] args) {
		// takes to method for populating the users
		populateTechnicians();
		new cinco();
	}

	public cinco() {
		boolean exit = false;

		// System.out.print("\033[H\033[2J");
		// System.out.flush();
		while (!exit) {
			output("CINCO MAIN MENU");
			output("===============");
			output("1. Login");
			output("2. Reset password");
			output("3. Create new account");
			output("4. Quit");

			try {
				int value = getInputInt("Please select an option (1-4)");
				switch (value) {
				case 1:
					this.displayLogin();
					break;
				case 2:
					this.displayPasswordReset();
					break;
				case 3:
					this.displayAccountCreation();
					break;
				case 4:
					exit = true;
					break;
				case 5:
					testTickets();
					break;
				case 6:
					expireClosedTix();
					break;
				default:
					System.err.println("Invalid option");
					break;
				}

			} catch (java.lang.NumberFormatException e) {
				System.err.println("Invalid option");
			}
		}

		System.exit(0);
	}

	/**
	 * For testing only: auto expires all closed tickets.
	 */
	private void expireClosedTix() {
		Calendar cal = Calendar.getInstance();
		for (ticket t : listOfTickets) {
			if (t.getStatus() != TicketStatus.OPEN) {
				System.out.println(String.format("Archiving ticket ID: %s... that was created at %s, and closed at %s", t.getTicketID(), t.getCreated(), t.getClosedDate()));
				cal.setTime(t.getClosedDate());
				cal.add(Calendar.HOUR, ticket.EXPIRY_TIME_IN_HOURS * -1);
				t.setClosedDate(cal.getTime());

				cal.setTime(t.getCreated());
				cal.add(Calendar.HOUR, ticket.EXPIRY_TIME_IN_HOURS * -1);
				t.setCreated(cal.getTime());
			}
		}
	}

	private void testTickets() {

		for (int i = 0; i != 10; i++) {
			String userName = "Test" + i, ticketName = "Test" + i, descript = "Test" + i;
			Severity severityEnum = Severity.LOW;

			int ticketID = listOfTickets.size() + 1;
			listOfTickets.add(new ticket(ticketID, userName, ticketName, descript, TicketStatus.OPEN, severityEnum));
			findTechnician(listOfTickets.get(listOfTickets.size() - 1));
		}

		Random r = new Random();
		int ticketID = listOfTickets.size() + 1;
		ticket t = null;
		ArrayList<User> staffCollection = new ArrayList<User>();
		ArrayList<User> techCollection = new ArrayList<User>();
		for (User user : listOfUsers) {
			if (user.getGroup() == Group.STAFF_MEMBER) {
				staffCollection.add(user);
				continue;
			}

			if (user.getGroup() == Group.TECHNICIAN) {
				techCollection.add(user);
				continue;
			}
		}
		User[] staffUsers = staffCollection.toArray(new User[0]);
		// User[] techUsers = techCollection.toArray(new User[0]);
		Calendar cal;

		// Create some older, closed tickets
		for (int i = 0; i != 10; i++) {
			Severity severityEnum = Severity.values()[r.nextInt(Severity.values().length)]; // Random severity 0–3
			TicketStatus status = TicketStatus.values()[r.nextInt(2)]; // Random closed ticket status 0–1
			String creatorUserName = staffUsers[r.nextInt(staffUsers.length)].getUserName();
			String ticketName = String.format("Test Ticket %s", ticketID);
			t = new ticket(ticketID, creatorUserName, ticketName, "", status, severityEnum);

			// Set a closed date within the past EXPIRY_TIME_IN_HOURS hours
			int hours = r.nextInt(ticket.EXPIRY_TIME_IN_HOURS - 1);
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, hours * -1);
			t.setClosedDate(cal.getTime());

			// Update creation date to make sense (about 7–14 days)
			int rewind = r.nextInt(7 * 24) + 7 * 24;
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, rewind * -1);
			t.setCreated(cal.getTime());

			String ticketDescription = String.format("Random closed ticket with severity of %s, and status of %s, created %s, closed %s",
			severityEnum, ticket.TicketStatusName[status.ordinal()], t.getCreated(), t.getClosedDate());
			t.setDescription(ticketDescription);

			listOfTickets.add(t);
			findTechnician(listOfTickets.get(listOfTickets.size() - 1));

			ticketID++;
		}

		// Create some even older, archived tickets
		for (int i = 0; i != 10; i++) {
			Severity severityEnum = Severity.values()[r.nextInt(Severity.values().length)]; // Random severity 0–3
			TicketStatus status = TicketStatus.values()[r.nextInt(2)]; // Random closed ticket status 0–1
			String creatorUserName = staffUsers[r.nextInt(staffUsers.length)].getUserName();
			String ticketName = String.format("Test Ticket %s", ticketID);
			t = new ticket(ticketID, creatorUserName, ticketName, "", status, severityEnum);

			// Set a closed date beyond EXPIRY_TIME_IN_HOURS
			int hours = r.nextInt(24 * 365) + ticket.EXPIRY_TIME_IN_HOURS;
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, hours * -1);
			t.setClosedDate(cal.getTime());

			// Update creation date to make sense (about 7–14 days)
			int rewind = r.nextInt(7 * 24) + hours;
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, rewind * -1);
			t.setCreated(cal.getTime());

			String ticketDescription = String.format("Random closed ticket with severity of %s, and status of %s, created %s, closed %s",
			severityEnum, ticket.TicketStatusName[status.ordinal()], t.getCreated(), t.getClosedDate());
			t.setDescription(ticketDescription);

			listOfTickets.add(t);
			findTechnician(listOfTickets.get(listOfTickets.size() - 1));

			ticketID++;
		}
	}

	private void displayAccountCreation() {
		// System.out.print("\033[H\033[2J");
		// System.out.flush();

		output("CREATE NEW ACCOUNT");
		output("==================");
		output("Press Return/Enter to exit");
		boolean exit = false;

		Authenticator auth = new Authenticator(cinco.listOfUsers);
		String message = "Please enter your email";
		String userName = null, password = null, passwordConfirmation = null, phoneNumber = null, firstName = null,
				lastName = null;
		Group group = null;
		User user = null;

		while (!exit) {

			String response = getInputString(message);

			if (response == null) {
				exit = true;
				break;
			}

			// Check if username already exists
			if (auth.usernameExists(response)) {
				System.err.println("Email exists. Please pick a new one.");
				continue;
			}

			// Populate username and move onto next question
			if (userName == null) {
				userName = response;
				message = "Please enter your password \n"
						+ "Password must be a minimum of 8 characters\n"
						+  "Must user upper and lowercase letters and numbers";
				continue;
			}

			// Populate password and move onto next question
			if (password == null) {
				boolean verified = false;
				String input = response;
				// verifies password meets acceptable criteria
				while (!verified) {
					verified = passwordVerify(input);
					if (!verified) {
						input = getInputString("Please enter your password");
					}
				}
				password = input;
				message = "Please confirm your password";
				continue;

			}

			// Check if passwords match. If not, go back to asking for password
			if (passwordConfirmation == null && !response.equals(password)) {
				System.err.println("Passwords do not match. Please try again!");
				message = "Please enter your password";
				password = null;
				continue;

			} else if (passwordConfirmation == null && response.equals(password)) {
				// Otherwise, continue to next question...
				passwordConfirmation = response;
				message = "Please enter your First Name";
				continue;
			}

			if (firstName == null) {
				firstName = response;
				message = "Please enter your Last Name";
				continue;
			}

			if (lastName == null) {
				lastName = response;
				message = "Please enter your 10 digit Phone Number, including Australian area code."
						+ "Eg. 04 mobile, 03 Victoria";
				continue;
			}

			if (phoneNumber == null) {
				boolean validph = verifyPhone(response);
				
				while(!validph) {
					String input = getInputString("Please enter your 10 digit Phone Number, including Australian area code."
							+ "Eg. 04 mobile, 03 Victoria");
					validph = verifyPhone(input);
				}
					
				phoneNumber = response;
				message = "Are you a\nS) Staff Member, or a \nT) Technician?\nPlease choose S or T";
				continue;
			}

			switch (response) {
			case "S":
			case "s":
				group = Group.STAFF_MEMBER;
				user = new StaffMember();
				break;

			case "T":
			case "t":
				group = Group.TECHNICIAN; // needs to add level if a technician
				user = new technician();
				break;

			default:
				System.err.println("Invalid response");
				continue;
			}

			// Store the new user
			assert (user != null && userName != null && password != null && passwordConfirmation != null
					&& group != null && phoneNumber != null && firstName != null && lastName != null); // None of these
																										// should be
																										// null by now.
			user.setUserName(userName);
			user.setPassword(password);
			user.setGroup(group);
			user.setPhNumber(phoneNumber);
			user.setFName(firstName);
			user.setLName(lastName);
			auth.addUser(user);
			exit = true;
			output("User created!");
		}
	}

	private boolean verifyPhone(String phonenumber) {
		char index = '0';
		
		for(int i=0; i<phonenumber.length(); i++)
			index = phonenumber.charAt(i);
			if(!Character.isDigit(index)) {
				System.err.println("\n Phone number must be digits only");
				return false;
			}
		
		if(phonenumber.length() !=10) {
			System.err.println("\n Phone number must be 10 digits including Australian area code.");
			return false;
		}
		
	
			return true;
	}

	private void displayPasswordReset() {
		// System.out.print("\033[H\033[2J");
		// System.out.flush();
		boolean exit = false;
		boolean validPassword = false;

		Authenticator auth = new Authenticator(cinco.listOfUsers);

		while (!exit) {
			output("RESET PASSWORD");
			output("==============");
			output("Press Return/Enter twice to exit");

			String email = getInputString("Please enter your email/username");

			while(!auth.usernameExists(email)) {
				email = getInputString("User not found! Please re-enter email/username");
			}

			String password = getInputString("Please enter your new password \n"
					+ "Password must be a minimum of 8 characters\n"
					+  "Must user upper and lowercase letters and numbers");
			validPassword = passwordVerify(password);



			while (!validPassword) {
				password = getInputString("Please enter your new password");
				validPassword = passwordVerify(password);
			}

			String passwordVerify = getInputString("Please re-enter your new password");

			if ((email == null && password == null) || (!password.equals(passwordVerify))) {
				exit = true;
				break;
			}

			try {
				auth.resetPassword(email, password);
				output("Your password has been reset successfully");
				exit = true;
			} catch (InvalidUserNameOrPasswordException nex) {
				System.err.println("Invalid username/password entered!");
			} catch (Exception ex) {
				System.err.println("An error occured: " + ex.getMessage());
				System.exit(1);
			}
		}
	}

	private void displayLogin() {
		// System.out.print("\033[H\033[2J");
		// System.out.flush();
		boolean exit = false;

		Authenticator auth = new Authenticator(cinco.listOfUsers);

		while (!exit) {
			output("LOGIN");
			output("=====");
			output("Press Return/Enter twice to exit");

			String email = getInputString("Please enter your email/username");
			String password = getInputString("Please enter your password");

			if (email == null && password == null) {
				exit = true;
				break;
			}

			try {
				AuthenticationResult result = auth.authenticate(email, password);
				this.displayUserMenuStub(result);
				exit = true;
			} catch (InvalidUserNameOrPasswordException nex) {
				System.err.println("Invalid username/password entered!");
			} catch (Exception ex) {
				System.err.println("An error occured: " + ex.getMessage());
				System.exit(1);
			}
		}
	}

	private void displayUserMenuStub(AuthenticationResult result) {
		switch (result.getUser().getGroup()) {
		case STAFF_MEMBER:
			showStaffMenu(result);
			break;
		case TECHNICIAN:
			showTechMenu(result);
			break;

		}
	}

	private void showStaffMenu(AuthenticationResult result) {
		boolean exit = false;
		while (!exit) {
			output("******Staff Menu******");
			output(result.getUser().getUserName() + " Please select from the following options");
			// display options for technicians
			output("1. Create ticket");
			output("2. View Staff created tickets");
			output("3. log out");
			try {
				int techMenuSelection = getInputInt("Type a number to select the option");

				switch (techMenuSelection) {
				case 1: // create ticket
					createTicket(result);
					break;
				case 2: //View staff's tickets
					viewTicketsAsStaff(result);
					break;
				case 3: // back to log in page
					exit = true;
					break;
				default:
					System.err.println("Invalid option");
					break;
				}

			} catch (java.lang.NumberFormatException e) {
				System.err.println("Invalid option");
			}
		}

	}
	private void viewTicketsAsStaff(AuthenticationResult result) {

		StaffMember temp = (StaffMember) result.getUser();
			
			List<ticket> filtered = Authenticator.findStaffTickets(listOfTickets, result.getUser());
			if (filtered.size() == 0) {
				output("no tickets for this user");
			}
			else {
				ticket.printTicketHeader();
				for (int i = 0; i < filtered.size(); i++) {
				filtered.get(i).printStaffTicket(temp);
			}
			}
			
		}


	public void showTechMenu(AuthenticationResult result) {
		boolean exit = false;
		while (!exit) {
			output("******Technician Menu******");
			output(result.getUser().getUserName() + " Please select from the following options");
			// display options for technicians
			output("1. View all tickets");
			output("2. Change ticket severity");
			// output("3. View assigned tickets");
			output("3. Change ticket status");
			output("4. Produce ticket report");
			output("5. log out");
			try {
				int techMenuSelection = getInputInt("Type a number to select the option");

				switch (techMenuSelection) {
				case 1: // view ticket
					viewTicketsAsTechnician(result);
					break;
				case 2: // change severity
					changeSeverity(result);
					break;
				case 3:
					changeTicketStatus(result);
					break;
				case 4:
					viewTicketReport(result);
					break;
				case 5: // back to log in page
					exit = true;
					break;
				default:
					System.err.println("Invalid option");
					break;
				}

			} catch (java.lang.NumberFormatException e) {
				System.err.println("Invalid option");
			}
		}
	}


	private void changeTicketStatus(AuthenticationResult result) {
		boolean exit = false;
		ticket editing = null;
		String ticketNumber = null;

		List<ticket> tickets = Authenticator.findTickets(listOfTickets, result.getUser());
		Map<String, ticket> validTicketIds = new HashMap<String, ticket>();
		Map<String, TicketStatus> validTicketStatuses = new HashMap<String, TicketStatus>();

		validTicketStatuses.put("1", TicketStatus.CLOSED_RESOLVED);
		validTicketStatuses.put("2", TicketStatus.CLOSE_UNRESOLVED);
		validTicketStatuses.put("3", TicketStatus.OPEN);

		output("CHANGE TICKET STATUS");
		output("====================");

		if (tickets.size() < 1) {
			System.out.println("No tickets assigned");
			exit = true;
		}

		while (!exit) {
			if (editing == null) {
				/**
				 * Select a ticket to edit
				 */
				System.out.println(ticket.getTicketHeader());
				for (ticket t : tickets) {
					System.out.println(String.format("%s", t));
					validTicketIds.put(Integer.toString(t.getTicketID()), t);
				}

				// String response = getInputString(String.format("Please enter the ticket
				// number (1–%s), or enter/return to exit", i));
				String response = getInputString("Please enter a ticket ID, or enter/return to exit");
				if (response == null || isBlank(response)) {
					exit = true;
					continue;
				}

				if (!validTicketIds.containsKey(response)) {
					System.err.println("Invalid response. Please try again.");
					continue;
				}
	

				editing = validTicketIds.get(response);
				ticketNumber = response;
			} else {
				/**
				 * Edit the selected ticket
				 */
				System.out.println(String.format("\n# Currently editing ticket: %s", ticketNumber));
				System.out.println("USER: " + editing.getUserName());
				System.out.println("NAME: " + editing.getTicketName());
				System.out.println("DESCRIPTION: " + editing.getDescription());
				System.out.println("SEVERITY: " + editing.getSeverity());
				System.out.println("STATUS: " + ticket.TicketStatusName[editing.getStatus().ordinal()]);

				String response = getInputString(
						"Choose new status: 1) Closed and Resolved, 2) Closed and Unresolved or 3) Open. Press return/enter to cancel change.");
				if (response == null || isBlank(response)) {
					editing = null;
					ticketNumber = null;
					continue;
				}
				if (validTicketStatuses.get(response) == editing.getStatus()) {
					System.err.println("Status is already "+ editing.getStatus());
				}

				if (!validTicketStatuses.containsKey(response)) {
					System.err.println("Invalid response. Please try again.");
					continue;
				}

				editing.closeTicket(validTicketStatuses.get(response));
				exit = true;
			}
		}
	}

	private void printReportDetail(TimeSpan ts, AuthenticationResult result) {
		String userName = result.getUser().getUserName();
		Date start = ts.startDate;
		Date end = ts.endDate;

		int i = 0;
		int outstanding = 0;
		int resolved = 0;
		for (ticket t : listOfTickets) {
			if (t.getCreated().after(start) && t.getCreated().before(end) && t.getTechName().equals(userName)) {
				// ticket is outstanding if OPEN as per Nebs response on discussion board
				// Outstanding is a ticket which has been submitted by a staff member,
				// but not attended to by any technician to date (i.e. not closed).
				if (t.getStatus() == TicketStatus.OPEN) {
					outstanding++;
				}
				if (t.getStatus() == TicketStatus.CLOSE_UNRESOLVED) {
					resolved++;
				}

				i++;
			}

		}
		output("TICKET STATS:");
		output("============");
		// showing how many tickets were submitted in that period, and out of those,
		// how many have been resolved and how many are outstanding
		output("Tickets submitted in this period: " + i);
		output("Outstanding tickets in this period: " + outstanding);
		output("Resolved tickets in this period: " + resolved);

		if (outstanding >= 1) {
			output("OUTSTANDING TICKETS:");
			output("===================");
			ticket.printReportTicketHeader();
			for (ticket t : listOfTickets) {
				if (t.getCreated().after(start) && t.getCreated().before(end) && t.getTechName().equals(userName)) {
					if (t.getStatus() == TicketStatus.OPEN) {
						// For all outstanding tickets, the report must show who submitted it
						// and when, and the severity of the ticket.
						t.printTicketsTechReport(userName);
					}
				}
			}
		}

		if (resolved >= 1) {
			output("RESOLVED TICKETS:");
			output("=================");
			ticket.printReportResolvedTicketHeader();
			for (ticket t : listOfTickets) {
				if (t.getCreated().after(start) && t.getCreated().before(end) && t.getTechName().equals(userName)) {

					if (t.getStatus() == TicketStatus.CLOSE_UNRESOLVED) {
						// For all resolved tickets,the report must show who submitted it
						// and when, who attended to it and how long it took to resolve it.
						t.printTicketsTechReportResolve(userName);
					}
				}
			}

		}
	}
	
	private void printReportDetail(Date timeFrame, AuthenticationResult result) {
		String userName = result.getUser().getUserName();
		
		
        int i = 0;
        int outstanding = 0;
        int resolved = 0;
    	for (ticket t : listOfTickets) {
    		if ((t.getCreated().after(timeFrame)) && t.getTechName().equals(userName)) {
    			//ticket is outstanding if OPEN as per Nebs response on discussion board
    			//Outstanding is a ticket which has been submitted by a staff member, 
    			//but not attended to by any technician to date (i.e. not closed).
    			if(t.getStatus() == TicketStatus.OPEN) {
					outstanding++;
				}
				if(t.getStatus() == TicketStatus.CLOSE_UNRESOLVED) {
					resolved++;
				}
				
				i++;
			}	
    		
    	}
    	output("TICKET STATS:");
    	output("============");
    	//showing how many tickets were submitted in that period, and out of those,
		//how many have been resolved and how many are outstanding
    	output("Tickets submitted in this period: "+i);
    	output("Outstanding tickets in this period: "+outstanding);
    	output("Resolved tickets in this period: "+resolved);
    	
    	if(outstanding >= 1) {
    	output("OUTSTANDING TICKETS:");
    	output("===================");
    	ticket.printReportTicketHeader();
    	for (ticket t : listOfTickets) {
    		if ((t.getCreated().after(timeFrame)) && t.getTechName().equals(userName)) {
    			if(t.getStatus() == TicketStatus.OPEN) {
    				//For all outstanding tickets, the report must show who submitted it
    				//and when, and the severity of the ticket.
    				t.printTicketsTechReport(userName);
				}
			}		
    	}
    }
    	
    	if(resolved >= 1) {
    	output("RESOLVED TICKETS:");
    	output("=================");
    	ticket.printReportResolvedTicketHeader();
    	for (ticket t : listOfTickets) {
    		if ((t.getCreated().after(timeFrame)) && t.getTechName().equals(userName)) {
    			
				if(t.getStatus() == TicketStatus.CLOSE_UNRESOLVED) {
					//For all resolved tickets,the report must show who submitted it 
					//and when, who attended to it and how long it took to resolve it.
					t.printTicketsTechReportResolve(userName);
				}
			}	
    	}
    		
    }
}

	private void viewTicketReport (AuthenticationResult result) {
		
		boolean exit = false;

			output("VIEW TICKET REPORT");
			output("====================");
			
			while (!exit) {

				int reportTime = getInputInt("Choose ticket report period time\n(1) 1 day (2) 3 days (3) 1 week (4) 1 fortnight (5) 1 month (6) Custom");
				if (reportTime == 0) {
					exit = true;
					break;
				}
				
				try {
					switch (reportTime) {
					case 1:
					        Calendar cal = Calendar.getInstance();
					        cal.add(Calendar.DATE, -1);
					        Date oneDay = cal.getTime();
					        
					        output("1 DAY");
							output("=====");
							printReportDetail(oneDay, result);
							
						break;

					case 2:
						//output("1 week");
						
						Calendar calThree = Calendar.getInstance();
						calThree.add(Calendar.DATE, -3);
				        Date threeDays = calThree.getTime();
				  
				        output("3 DAYS");
						output("======");
						printReportDetail(threeDays, result);
							
						break;
						
					case 3:
						Calendar calWeek = Calendar.getInstance();
						calWeek.add(Calendar.DATE, -7);
				        Date oneWeek = calWeek.getTime();
				  
				        output("1 WEEK");
						output("======");
						printReportDetail(oneWeek, result);
							
						break;
						
					case 4:
						Calendar calFortnight = Calendar.getInstance();
						calFortnight.add(Calendar.DATE, -14);
				        Date fortnight = calFortnight.getTime();
				  
				        output("1 FORTNIGHT");
						output("===========");
						printReportDetail(fortnight, result);
						
						break;

					case 5:
						Calendar calMonth = Calendar.getInstance();
						calMonth.add(Calendar.DATE, -30);
					    Date month = calMonth.getTime();
					    
					    output("1 MONTH");
						output("=======");
						printReportDetail(month, result);
						break;
					case 6:
						TimeSpan ts = this.getTimeSpan();
						if (ts != null) {
							output(String.format("%s", ts));
							output("=====================");
							printReportDetail(ts, result);
						}

						break;
						
					default:
						output("invalid option");
						continue;
					}
				} catch (java.lang.NumberFormatException e) {
					System.err.println("Invalid option");
				}

				
				break;
			}
		}

	private TimeSpan getTimeSpan() {
		boolean exit = false;
		TimeSpan ts = null;
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

		while (!exit) {
			try {
				if (ts == null) {
					String dateString = getInputString("Please enter Start Date in YYYY-MM-DD");
					if (dateString == null) {
						break;
					}
					java.util.Date date = parser.parse(dateString);
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

					ts = new TimeSpan();					
					ts.startDate = cal.getTime();
				}
	
				if (ts.endDate == null) {
					String dateString = getInputString("Please enter End Date in YYYY-MM-DD");
					if (dateString == null) {
						break;
					}
					java.util.Date date = parser.parse(dateString);
					if (date.before(ts.startDate)) {
						System.err.println("End date can not be before start date");
						continue;
					}
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.HOUR_OF_DAY, 24);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 59);
					cal.set(Calendar.MILLISECOND, 999);

					ts.endDate = cal.getTime();
				}

				return ts;
			} catch (ParseException pe) {
				System.err.println("Invalid date entered");
			} catch (Exception ex) {
				System.err.println("An error occured: " + ex.getMessage());
				exit = true;
			}
		}

		return null;
	}

	private void viewTicketsAsTechnician(AuthenticationResult result) {
		List<ticket> tickets = Authenticator.findTickets(listOfTickets, result.getUser(), true); // Include archived tix as well
		if (tickets.size() < 1) {
			System.out.println("No tickets assigned");
			return;
		}

		System.out.println(ticket.getTicketHeader());
		for (ticket t : tickets) {
			System.out.println(String.format("%s", t));
		}
	}

	private void createTicket(AuthenticationResult result) {
		boolean exit = false;

		while (!exit) {
			output("CREATE TICKET");
			output("==============");

			String userName = result.getUser().getUserName();
			String ticketName = getInputString("Enter ticket name");
			String descript = getInputString("Enter ticket description");
			if ((descript == null) || (ticketName == null)) {
				output("---------------Ticket information needed----------------");
				exit = true;
				break;
			}
			while (!exit) {

				int severity = getInputInt("Choose severity level\n(1) Low (2) Medium (3) High");
				if (severity == 0) {
					exit = true;
					break;
				}
				TicketStatus status = TicketStatus.OPEN;
				Severity severityEnum = null;
				try {
					switch (severity) {
					case 1:
						severityEnum = Severity.LOW;
						break;

					case 2:
						severityEnum = Severity.MEDIUM;
						break;

					case 3:
						severityEnum = Severity.HIGH;
						break;

					default:
						output("invalid option");
						continue;
					}
				} catch (java.lang.NumberFormatException e) {
					System.err.println("Invalid option");
				}

				// find current numb and add ticket id
				int currentTicket = listOfTickets.size() + 1;
				// add ticket to system here
				listOfTickets.add(new ticket(currentTicket, userName, ticketName, descript, status, severityEnum));
				// finds the index of the last added Ticket in the List
				int index = listOfTickets.size() - 1;
				findTechnician(listOfTickets.get(index));
				addTicketToStaff(result);
				output("Your ticket has been successfully added to the system with a status of OPEN");
				break;
			}
			break;
		}
	}

	private void addTicketToStaff(AuthenticationResult result) {
		StaffMember staff = new StaffMember();
		if (staff.getUserName() == result.getUser().getUserName()) {
			staff.increaseStaffTicketCount();
			int display = staff.getStaffTicketCount();
			output("number of tickets for staff" +display);
		}


	}

	private void findTechnician(ticket Ticket) {
		// Method to find technician with appropriate criteria.
		technician s = new technician();
		List<technician> listOfTechs = new ArrayList<technician>();
		int highestNumber = 0;
		int lowestNumber = 999;
		Severity tempSev = Ticket.getSeverity();

		// looks through the list of users
		for (int i = 0; i < listOfUsers.size(); i++) {
			// checks if the user is a technician
			if (listOfUsers.get(i).getClass() == s.getClass()) {
				// adds Technicians into own list.
				s = (technician) listOfUsers.get(i);
				listOfTechs.add(s);
			}
		}

		// compares severity and level of technicians and removes those which don't met
		// the criteria.
		// if its a High severity ticket then if will remove the level 1 techs.
		if (tempSev == Severity.HIGH) {
			for (int i = 0; i < listOfTechs.size(); i++) {
				s = listOfTechs.get(i);
				if (s.getLevel() == 1) {
					listOfTechs.remove(i);
					i--;
				}
			}
		} else {
			// otherwise its a level one tech support issue so remove the level 2 techs.
			for (int i = 0; i < listOfTechs.size(); i++) {
				s = listOfTechs.get(i);
				if (s.getLevel() == 2) {
					listOfTechs.remove(i);
					if (i > 0) {
						i--;
					}
				}
			}
		}
		// looks through list of technicians to find lowest number of tickets amongst
		// group.
		for (technician Tech : listOfTechs) {
			if (Tech.getTicketCount() > highestNumber) {
				highestNumber = Tech.getTicketCount();
			}

			if (Tech.getTicketCount() < lowestNumber) {
				lowestNumber = Tech.getTicketCount();
			}
		}

		if (lowestNumber != highestNumber) {
			for (int i = 0; i < listOfTechs.size(); i++) {
				s = listOfTechs.get(i);

				if (highestNumber == s.getTicketCount()) {
					listOfTechs.remove(i);
					i--;
				}
			}
		}
		// Choose random integer out of the number of Technicians available.
		// First check is there is only 1 object in the list. If there is assign to that
		// tech.
		if (listOfTechs.size() == 1) {
			s = listOfTechs.get(0);
			Ticket.setTechnician(s);
			// Otherwise choose randomly between techs in the list.
		} else {
			Random rand = new Random();
			int RandInt = rand.nextInt(listOfTechs.size());
			s = listOfTechs.get(RandInt);
			Ticket.setTechnician(s);
		}
	}

	private void changeSeverity(AuthenticationResult result) {
		// change severity of ticket code here
		boolean exit = false;
		int severityChange = 0;

		while (!exit) {
			output("CHANGE TICKET SEVERITY");
			output("======================");

			// first show tickets to user
			technician temp = (technician) result.getUser();
			int ticketCount = temp.getTicketCount();
			if (ticketCount == 0) {
				output("You have no tickets assigned");
				exit = true;
				break;
			} else {
				ticket.printTicketHeader();
				// show technicians tickets
				List<ticket> filtered = Authenticator.findTickets(listOfTickets, result.getUser());
				for (int i = 0; i < filtered.size(); i++) {
					filtered.get(i).printTechTickets(temp);
				}

				boolean choice = false;
				while (!choice) {
					severityChange = getInputInt("Enter ID of ticket to change its severity");
					// change the severity of ticket chosen here
					for (ticket temp1 : filtered) {
						if (severityChange == temp1.getTicketID()) {
							choice = true;
						}
					}
					if (!choice) {
						output("Invalid Option");
					}
				}

				int severity = getInputInt("Choose severity level\n(1) Low (2) Medium (3) High");
				if (severity == 0) {
					exit = true;
					break;
				}
				Severity severityEnum = null;
				try {
					switch (severity) {
					case 1:
						severityEnum = Severity.LOW;
						break;

					case 2:
						severityEnum = Severity.MEDIUM;
						break;

					case 3:
						severityEnum = Severity.HIGH;
						break;

					default:
						output("invalid option");
						continue;
					}
				} catch (java.lang.NumberFormatException e) {
					System.err.println("Invalid option");
					exit = true;
				}
				// must add/replace ticket here with changed severity
				for (int i = 0; i < listOfTickets.size(); i++) {
					if (listOfTickets.get(i).getTicketID() == (severityChange)) {
						// change severity below to severity inputed by user above
						listOfTickets.get(i).setSeverity(severityEnum);
						// reassign ticket based on new severity
						findTechnician(listOfTickets.get(i));
					}
				}

				// reassign ticket by calling findTechnichian method and passing changed ticket
				exit = true;
				break;

			}

		}
	}

	public static void populateTechnicians() {
		// populates technicians
		listOfUsers.add(new technician("harrystyles", "test", Group.TECHNICIAN, 1));
		// password for harry is test
		listOfUsers.add(new technician("niallhoran", "horan1", Group.TECHNICIAN, 1));
		// password for nial is horan1
		listOfUsers.add(new technician("liampayne", "liam123", Group.TECHNICIAN, 1));
		// password for liam is liam123
		listOfUsers.add(new technician("louistomlinson", "tommy", Group.TECHNICIAN, 2));
		// password for louis is tommy
		listOfUsers.add(new technician("zaynmalik", "zm321", Group.TECHNICIAN, 2));
		// password for zayn is zm321

		technician user = new technician();
		user.setUserName("tech@cinco.com");
		user.setGroup(Group.TECHNICIAN);
		user.setPassword("password123");
		user.setLevel(1);
		listOfUsers.add(user);

		StaffMember testuser = new StaffMember();
		testuser.setUserName("user@cinco.com");
		testuser.setGroup(Group.STAFF_MEMBER);
		testuser.setPassword("Test123");
		listOfUsers.add(testuser);
	}

	public boolean passwordVerify(String password) {
		boolean result = false;
		boolean hasUpper = false;
		boolean hasLower = false;
		boolean hasNumber = false;
		char c = '0';

		if (password == null || password.length() < 8) {
			output("Password too short, please use a minimum of 8 characters");
			return result;
		}

		else {
			for (int i = 0; i < password.length(); i++) {
				c = password.charAt(i);
				if (Character.isLowerCase(c)) {
					hasLower = true;
				}
				if (Character.isUpperCase(c)) {
					hasUpper = true;
				}
				if (Character.isDigit(c)) {
					hasNumber = true;
				}
			}
			if (hasNumber && hasLower && hasUpper) {
				result = true;
				return result;
			}
		}
		output("Password Invalid \n"
				+ "Password must be a minimum of 8 characters\n"
				+  "Must user upper and lowercase letters and numbers.");
		return result;
	}

	public void output(String message) {
		// Method to make showing an output easier and more efficient
		System.out.println("\n" + message);

	}

	public String getInputString(String message) {
		// Method to get a string input
		String result = null;
		System.out.print("\n" + message + ": ");
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		result = keyboard.nextLine();
		// if (result != null && result.isBlank())
		// result = null;
		if (this.isBlank(result))
			result = null;

		return result;

	}

	/**
	 * Shim function for Java 8 to check for blank input
	 *
	 * @param str text to check
	 * @return whether or not the string is entirely blank
	 */
	private boolean isBlank(String str) {
		if (str == null || str.length() < 1) {
			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false; // Non-whitespace character found, therefore not blank
			}
		}

		return true;
	}

	public char getInputChar(String message) {
		// method to get a char input
		return (getInputString(message)).charAt(0);
	}

	public double getInputDouble(String message) {
		// method to get a double input
		return Double.parseDouble(getInputString(message));
	}

	public int getInputInt(String message) {
		// Method to get an integer input
		return Integer.parseInt(getInputString(message));
	}

	public boolean getInputBoolean(String message) {
		// method to return a boolean
		String input = getInputString(message);
		if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")
				|| input.equalsIgnoreCase("t")) {
			return true;
		} else {
			return false;
		}
	}
}
