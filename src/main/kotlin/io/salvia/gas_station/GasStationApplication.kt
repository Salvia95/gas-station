package io.salvia.gas_station

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulithic

@Modulithic
@SpringBootApplication
class GasStationApplication

fun main(args: Array<String>) {
	runApplication<GasStationApplication>(*args)
}
