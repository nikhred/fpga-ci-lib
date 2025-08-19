# Arguments: --top <top> --part <part> --xdc <comma_list> --outdir <dir>
proc parse_args {argv} {
    array set args {--top "" --part "" --xdc "" --outdir build/xilinx}
    for {set i 0} {$i < [llength $argv]} {incr i} {
        set k [lindex $argv $i]
        if {[info exists args($k)]} {
            incr i
            set args($k) [lindex $argv $i]
        }
    }
    return [array get args]
}

set a [dict create {*}[parse_args $::argv]]
set top     [dict get $a --top]
set part    [dict get $a --part]
set xdc_raw [dict get $a --xdc]
set outdir  [dict get $a --outdir]

file mkdir $outdir

set_msg_config -id {Common 17-55} -suppress

read_verilog [glob -nocomplain rtl/**/*.v]
read_vhdl    [glob -nocomplain rtl/**/*.vhd]

if {$xdc_raw ne ""} {
    foreach xdc [split $xdc_raw ","] {
        if {[file exists $xdc]} { read_xdc $xdc }
    }
}

set_part $part
update_compile_order -fileset sources_1

synth_design -top $top -part $part
write_checkpoint -force $outdir/post_synth.dcp

opt_design
place_design
phys_opt_design
route_design
write_checkpoint -force $outdir/post_route.dcp

report_timing_summary -file $outdir/timing_summary.rpt -warn_on_violation -no_header -no_detailed_paths
report_utilization     -file $outdir/utilization.rpt

write_bitstream -force $outdir/${top}.bit

exit 0

