package cinco;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ticket {
	public static final int EXPIRY_TIME_IN_HOURS = 24;

	private int ticketID;
	private String ticketName;
	private String description;
	private Severity severity;
	private String userName;
	private technician Technician = new technician();
	private TicketStatus status;
	private Date created;
	private Date closed;

	/**
	 * Constants that define textual representations of the ticket status
	 */
	public static final String[] TicketStatusName = new String[] {
		"Closed, Resolved",
		"Closed, Unresolved",
		"Open",
	};

	public ticket() {
		this.created = new Date();
	}

	public ticket(int ticketID, String userName,String ticketName, String description, TicketStatus status, Severity severity) {
		this();
		this.setTicketID(ticketID);
		this.setUserName(userName);
		this.setTicketName(ticketName);
		this.setDescription(description);
		this.setSeverity(severity);
		this.closeTicket(status);
	}

	public boolean isArchived() {
		if (this.getStatus() == TicketStatus.OPEN) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(this.getClosedDate());
		cal.add(Calendar.HOUR, ticket.EXPIRY_TIME_IN_HOURS);
		Date ticketExpiryDate = cal.getTime();
		
		return !(ticketExpiryDate.after(now) || ticketExpiryDate.equals(now));
	}

	public Date getClosedDate() {
		return closed;
	}

	public Date getCreated() {
		return created;
	}

	//changePriorityTickets
	public int getTicketID() {
		return ticketID;
	}

	public String getTicketName() {
		return ticketName;
	}

	public String getDescription() {
		return description;
	}

	public Severity getSeverity() {
		return this.severity;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getTechName() {
		return this.Technician.getUserName();
	}

	public TicketStatus getStatus() {
		return this.status;
	}

	public void setClosedDate(Date closed) {
		this.closed = closed;
	}

	public void setTechnician(technician Tech) {
		this.Technician = Tech;
		this.Technician.increaseTicketCount();
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public void setTicketName(String ticketName) {
		this.ticketName=ticketName;
	}

	public void setDescription(String description) {
		this.description=description;
	}

	public void setUserName(String userName) {
		this.userName=userName;
	}
	
	public long getTimeDifference() {
		Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        Date closedDate = this.getClosedDate();
        //gets difference in milliseconds
        long diffInTime = today.getTime() - closedDate.getTime();
        
        //changes to seconds
        long seconds= diffInTime/1000;

        return seconds;
	}
	
	public long getMinDifference() {
		long seconds = getTimeDifference();
		long mins = seconds/60;
		return mins;
	}

	/**
	 * Closes the ticket and sets the closing timestamp to current date.
	 * @param status a valid close status
	 */
	public void closeTicket(TicketStatus status) {
		this.status = status;
		this.closed = new Date();
	}

	public static void printTicketHeader() {//prints the table header
		System.out.println(getTicketHeader());
		System.out.println("----------------------------------------------------------------------------------------------"+
		"-----------------------------------------------------------------------");
	}
	
	public static void printReportTicketHeader() {//prints the table header
		System.out.println(getReportTicketHeader());
		System.out.println("----------------------------------------------------------------------------------------------"+
		"-----------------------------------------------------------------------");
	}
	
	public static void printReportResolvedTicketHeader() {//prints the table header
		System.out.println(getReportResolvedTicketHeader());
		System.out.println("----------------------------------------------------------------------------------------------"+
		"-----------------------------------------------------------------------");
	}

	public static String getTicketHeader() {
		return String.format("|%-10s |%-20s |%-20s |%-30s |%-10s |%-30s", "ID",  "username",  "ticket name",
				 "status",  "severity",  "Tech");
	}
	
	public static String getReportTicketHeader() {
		return String.format("|%-10s |%-20s |%-30s |%-20s |%-20s |%-10s |%-30s", "ID",  "submitted by",   "created at",  "ticket name",
				 "status",  "severity",  "Tech");
	}
	
	public static String getReportResolvedTicketHeader() {
		return String.format("|%-10s |%-20s |%-30s |%-20s |%-30s |%-20s", "ID",  "submitted by",   "created at",  "ticket name",
				   "attended by", "time to resolve");
	}

	private String getStatusDescription() {
		String description = TicketStatusName[this.getStatus().ordinal()];

		if (this.isArchived()) {
			description = description + " (Archived)";
		}

		return description;
	}

	@Override
	public String toString() {
		String response = (String.format("|%-10s |%-20s |%-20s |%-30s |%-10s |%-30s \n", this.getTicketID(), this.getUserName(),
				this.getTicketName(),  this.getStatusDescription(), this.getSeverity(), this.getTechName()));
		response += "Description: "+ this.getDescription()+ "\n" + "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------";
		return response;
	}

	public void printTickets() {//format for printing the tickets
		System.out.println(String.format("|%-10s |%-20s |%-20s |%-30s |%-10s |%-30s \n", this.getTicketID(), this.getUserName(),
				this.getTicketName(), this.getStatusDescription(), this.getSeverity(), this.getTechName()));
		System.out.println("Description: "+ this.getDescription()+ "\n" + "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------");
	}

	public void printTicketsTech(String assignedTech) {//format for printing the tickets
		System.out.println(String.format("|%-10s |%-20s |%-20s |%-30s |%-10s |%-30s \n",  this.getTicketID(), this.getUserName(),
				this.getTicketName(), this.getStatusDescription(), this.getSeverity(), assignedTech));
		System.out.println("Description: "+ this.getDescription()+"\n"+ "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------");
	}

	public void printTicketsTechReport(String assignedTech) {//format for printing the tickets
		System.out.println(String.format("|%-10s |%-20s |%-30s |%-20s |%-20s |%-10s |%-30s \n",  this.getTicketID(), this.getUserName(),
				String.format("%1$tF %1$tR", this.getCreated()),this.getTicketName(), this.getStatus(), this.getSeverity(), assignedTech));
		System.out.println( "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------");
	}
	
	public void printTicketsTechReportResolve(String assignedTech) {//format for printing the tickets
		System.out.println(String.format("|%-10s |%-20s |%-30s |%-20s |%-30s |%-20s\n",  this.getTicketID(), this.getUserName(),
				String.format("%1$tF %1$tR", this.getCreated()),this.getTicketName(),assignedTech, this.getMinDifference() +" minutes"));
		System.out.println( "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------");
	}
	
	public void printTechTickets(technician tempTech) {
		if(tempTech.getUserName() == this.Technician.getUserName()) {
			printTicketsTech(tempTech.getUserName());
		}

	}
	public void printStaffTicket(StaffMember tempStaff) {
		if(tempStaff.getUserName() ==this.getUserName()) {
			printTicketsStaff(tempStaff.getUserName());
		}
	}

	public void printTicketsStaff(String userName) {
		System.out.println(String.format("|%-10s |%-20s |%-20s |%-30s |%-10s |%-30s \n",  this.getTicketID(), userName,
				this.getTicketName(), this.getStatusDescription(), this.getSeverity(), this.getTechName()));
		System.out.println("Description: "+ this.getDescription()+"\n"+ "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------");

	}

	public List<ticket> buildTable() { // the build for the table to be printed
		List<ticket> ticketList = new ArrayList<>();
		int i = 0;
		while(i<50) {
			ticketList.add(new ticket(this.getTicketID(), this.getUserName(), this.getTicketName(), this.getDescription(), this.getStatus(), this.getSeverity()));
			i++;
		}
		return ticketList;
	}
	public void setCreated(Date time) {
	this.created = time;
	}

	public void printTicketsStaff(StaffMember temp) {
		System.out.println(String.format("|%-10s |%-20s |%-20s |%-30s |%-10s |%-30s \n",  this.getTicketID(), temp,
				this.getTicketName(), this.getStatusDescription(), this.getSeverity(), this.getTechName()));
		System.out.println("Description: "+ this.getDescription()+"\n"+ "----------------------------------------------------------------------------------------------"+
				"-----------------------------------------------------------------------");


	}


}
