package com.challenge.upgrade.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import com.challenge.upgrade.demo.model.DayAvailability;
import com.challenge.upgrade.demo.model.Reservation;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class DemoApplicationTests {

	private static final int RESERVATION_THREADS = 200;
	private static final String HOST = "http://localhost:";
	private static final String RESERVATION_ENDPOINT = "/reservation/";
	private static final String AVAILABILITY_ENDPOINT = "/availability/";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	private static final int MAX_OCCUPANCY = 15;

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testBasicCreateAndRetrieveReservation() throws Exception {
		Reservation reservation = new Reservation();
		reservation.setEmail("test@gmail.com");
		reservation.setFullName("Fuller Namaste");
		LocalDate d1 = LocalDate.now().plusDays(1);
		reservation.setStartDate(d1.toString());
		LocalDate d2 = d1.plusDays(1);
		reservation.setEndDate(d2.toString());
		reservation.setNumberOfPeople(1);

		HttpEntity<Reservation> entity = new HttpEntity<Reservation>(reservation, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort(RESERVATION_ENDPOINT), entity,
				String.class);
		assertTrue(response.getStatusCode().is2xxSuccessful());

		int reservationId = Integer.valueOf(response.getBody());

		HttpEntity<String> entityRetrieve = new HttpEntity<String>(null, headers);
		response = restTemplate.exchange(createURLWithPort(RESERVATION_ENDPOINT + reservationId), HttpMethod.GET,
				entityRetrieve, String.class);
		JSONObject body = new JSONObject(response.getBody());
		assertEquals(reservationId, body.get("id"));
	}

	@Test
	public void testMultithreadedReservationCreation() throws InterruptedException {
		TestUtil.runMultithreaded(new Runnable() {
			public void run() {
				try {
					HttpEntity<Reservation> entity = new HttpEntity<Reservation>(createRandomDto(), headers);
					ResponseEntity<String> response = restTemplate
							.postForEntity(createURLWithPort(RESERVATION_ENDPOINT), entity, String.class);
					if (response.getStatusCode().is2xxSuccessful())
						System.out.println(ANSI_GREEN + response + ANSI_RESET);
					else
						System.out.println(ANSI_RED + response + ANSI_RESET);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, RESERVATION_THREADS);

	}

	@Test
	public void testOccupancyLimit() throws Exception {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURLWithPort(AVAILABILITY_ENDPOINT))
				.queryParam("startDate", LocalDate.now().plusDays(1).toString())
				.queryParam("endDate", LocalDate.now().plusMonths(1).toString());

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<List<DayAvailability>> response = restTemplate.exchange(builder.build().encode().toUri(),
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<DayAvailability>>() {
				});
		for (DayAvailability dayAvailability : response.getBody()) {
			assertTrue(dayAvailability.getOccupancy() <= MAX_OCCUPANCY);
		}
	}

	/**
	 * create url for requests
	 *
	 * @param uri
	 * @return
	 */
	private String createURLWithPort(String uri) {
		return HOST + port + uri;
	}

	/**
	 * Create random reservation
	 *
	 * @return
	 */
	public static Reservation createRandomDto() {
		Reservation dto = new Reservation();
		dto.setEmail(DemoApplicationTests.randomString());
		dto.setFullName(DemoApplicationTests.randomString());
		LocalDate d1 = DemoApplicationTests.randomLocalDate();
		dto.setStartDate(d1.toString());
		LocalDate d2 = d1.plusDays(2);
		dto.setEndDate(d2.toString());
		dto.setNumberOfPeople(ThreadLocalRandom.current().nextInt(1, 3 + 1));
		return dto;
	}

	/**
	 * Create random string for email and name
	 *
	 * @return
	 */
	public static String randomString() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}

	/**
	 * Create random LocalDate for reservation. On purpose date can be out of scope
	 * for reservation restrictions.
	 *
	 * @return
	 */
	public static LocalDate randomLocalDate() {
		long minDay = LocalDate.now().plusDays(1).toEpochDay();
		long maxDay = LocalDate.now().plusMonths(1).toEpochDay();
		long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
		return LocalDate.ofEpochDay(randomDay);
	}
}
