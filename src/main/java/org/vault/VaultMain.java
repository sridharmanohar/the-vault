package org.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultMain /*implements CommandLineRunner*/ {

	public static void main(String[] args) {
		SpringApplication.run(VaultMain.class, args);
	}
	
//	@Override
//	public void run(String... args) throws Exception {
//		System.out.println("in clr");
//		this.topicDetailRepo.save(new TopicDetail(1, "Kompally1984", 1, 35));
//	}
}
