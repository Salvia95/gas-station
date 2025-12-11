package io.salvia95.gas_station

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GasStationApplication

fun main(args: Array<String>) {
	runApplication<GasStationApplication>(*args)
}
