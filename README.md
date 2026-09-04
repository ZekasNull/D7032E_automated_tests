# D7032E_automated_tests

## Cheat sheet

Compile: `compile.sh ` 

Compile and replace with buggy PaymentImpl: `buggyCodeCompile.sh`

Run unit tests and generate coverage report: `unittest.sh`

Run with valid input data:  `run.sh` (after compiling)


## Requirements per the assignment

Your task is to run the system under test and verify that these requirements are correctly implemented. The implementation you have been given uses the current month to calculate payment date, but you have been asked to test the system for the spring-term of 2016 (2016-01-01 to 2016-06-30).

Age requirements

    [ID: 101] The student must be at least 20 years old to receive subsidiary and student loans.
    [ID: 102] The student may receive subsidiary until the year they turn 56.
    [ID: 103] The student may not receive any student loans from the year they turn 47.

Study pace requirements

    [ID: 201] The student must be studying at least half time to receive any subsidiary.
    [ID: 202] A student studying less than full time is entitled to 50% subsidiary.
    [ID: 203] A student studying full time is entitled to 100% subsidiary.

Income while studying requirements

    [ID: 301] A student who is studying full time or more is permitted to earn a maximum of 85 813SEK per year in order to receive any subsidiary or student loans.
    [ID: 302] A student who is studying less than full time is allowed to earn a maximum of 128 722SEK per year in order to receive any subsidiary or student loans.

Completion ratio requirement

    [ID: 401] A student must have completed at least 50% of previous studies in order to receive any subsidiary or student loans.

When and amount paid requirements

    Full time students are entitled to:
        [ID: 501] Student loan: 7088 SEK / month
        [ID: 502] Subsidiary: 2816 SEK / month
    Less than full time students are entitled to:
        [ID: 503] Student loan: 3564 SEK / month
        [ID: 504] Subsidiary: 1396 SEK / month
    [ID: 505] A person who is entitled to receive a student loan will always receive the full amount.
    [ID: 506] Student loans and subsidiary is paid on the last weekday (Monday to Friday) every month.

