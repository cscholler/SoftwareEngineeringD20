package edu.wpi.leviathans.views;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.db.DatabaseService;

@Slf4j
public class DatabaseViewController {
	@Inject
	DatabaseService db;
}
