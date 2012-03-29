* Contents:
	+ build.xml - ant build file

	+ build.number - current build number

	+ src - source folder (follows Maven folder structure)

		+ docs

		+ main - main sources

			+ java - java main sources (package folders start here)
				+ packages ...

			+ sql

			+ resource

		+ test - test sources

			+ java - java test sources (package folders start here)
				+ packages ...

			+ sql

			+ resource
	
	+ vendor/lib/ - external libraries

	+ target (created by compile tasks and removed by "clean" task) - .class and .jar files

		- shouldn't be on remote repository.