command rcd {
	region clear {
		perm rcd.use;
		help Clears all regions;
		run region_clear;
	}
	region define [string:name] {
		type player;
		perm rcd.use;
		help Defines a region;
		run region_define name;
	}
	region pos1 {
		type player;
		perm rcd.use;
		help Sets the first position for a region;
		run region_pos1;
	}
	region pos2 {
		type player;
		perm rcd.use;
		help Sets the second position for a region;
		run region_pos2;
	}
	region list {
			[int:page] {
				help Lists all regions;
				perm rcd.use;
				run region_list page;
			}
		help Lists all regions;
		perm rcd.use;
		run region_list_1;
	}
	region tp [string:name] {
		help Teleports you to a region;
		perm rcd.use;
		run region_tp name;
		type player;
	}
	region delete [string:name] {
		help Deletes a region;
		perm rcd.use;
		run region_del name;
	}
	list -a {
		[int:page] {
			run list_auto page;
			help Lists a page from an automatic scan;
			perm rcd.use;
		}
		run list_auto_1;
		help Lists the first page from an automatic scan;
		perm rcd.use;
	}
	tp -a [int:num] {
		perm rcd.use;
		type player;
		help Teleports to a clock found by an automatic scan;
		run tp_auto num;
	}
	scan [int:seconds] {
		perm rcd.use;
		type player;
		help Runs a scan for all redstone updates;
		run scan seconds;
	}
	list {
		[int:page] {
			run list page;
			help Lists a page from a manual scan;
			perm rcd.use;
			type player;
		}
		run list_first;
		help Lists the first page from a manual scan;
		perm rcd.use;
		type player;
		
	}
	tp [int:num] {
		perm rcd.use;
		type player;
		help Teleports to a clock found by a manual scan;
		run tp num;
	}
}