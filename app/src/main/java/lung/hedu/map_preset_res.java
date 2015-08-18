package lung.hedu;

/**
 * Created by Sarah on 17-8-2015.
 */
public class map_preset_res {
    public static String[] set_map_colors()
    {
        String[] map_colors = new String[19];

        // 0 = no available for walking
        map_colors[0] = "#444444";

        // 1 = available for walking
        map_colors[1] = "#448844";

        // 2 = player pos
        map_colors[2] = "#66ff66";

        return map_colors;
    }
}
