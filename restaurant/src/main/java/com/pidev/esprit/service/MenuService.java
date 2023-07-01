package com.pidev.esprit.service;

import com.pidev.esprit.model.Menu;
import com.pidev.esprit.model.MenuDesemaine;
import com.pidev.esprit.model.MenuPreferences;
import com.pidev.esprit.repository.MenuDeSemaineRepository;
import com.pidev.esprit.repository.MenuPreferencesRepository;
import com.pidev.esprit.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
public class MenuService {
    @Autowired
    private MenuPreferencesRepository menuPreferencesRepository;

    @Autowired

    private MenuRepository menuRepository;
    @Autowired
    private MenuDeSemaineRepository menuDeSemaineRepository;
    @Autowired
    JavaEmailService javaEmailService;
    public void SupprimerMenu(long id ){menuRepository.deleteById(id);}
    public void UpdateMenu(long id , Menu menu){
        Menu m;
        m=menuRepository.findAllById(id);
        m.setPrice(menu.getPrice());
        m.setDescription(menu.getDescription());
        m.setName(menu.getName());
        m.setAvailable(menu.getAvailable());
        m.setCalories(m.getCalories());
        menuRepository.save(menu);



    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuByName(String name) {
        Menu M =menuRepository.findByName(name);
        if(M==null){
            throw new NotFoundException("Menu n'existe pas" + name);
        }

        return M;
    }
    public Menu findMenu(long id ){return menuRepository.findAllById(id); }

    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }
    public Menu find(String name) {
        Menu M;
        M = menuRepository.findByName(name);
        return M;
    }


    public Menu updateMenu(String name,Menu menu2) {
        Menu M ;
        M=menuRepository.findByName(name);

            M.setName(menu2.getName());
            M.setAvailable(menu2.getAvailable());
            M.setDescription(menu2.getDescription());
            M.setPrice(menu2.getPrice());
            M.setCalories(menu2.getCalories());
            menuRepository.save(M);
            return M;

        /*Menu existingMenu = getMenuById(id);
        existingMenu.setName(menu.getName());
        existingMenu.setDescription(menu.getDescription());
        existingMenu.setPrice(menu.getPrice());
        existingMenu.setAvailable(menu.getAvailable());
        existingMenu.setCategory(menu.getCategory());
        return menuRepository.save(existingMenu);*/

    }
    public Double getMenuPriceByName(String menuName) {
        Menu menu = menuRepository.findByName(menuName);
        if (menu == null) {
            throw new RuntimeException("Menu not found");
        }
        return menu.getPrice();
    }

    public void deleteMenu(String name) {
        menuRepository.deleteByName(name);
    }
    public void DeletMenu(long id ){ menuRepository.deleteById(id);}
   @Scheduled(fixedRate = 20000000)
    public void GenererMenu() {
        ArrayList<Menu>menus = (ArrayList<Menu>) menuRepository.findAll();
        ArrayList<Menu>menuDeSemaine = new ArrayList<Menu>();
        int r =menus.size();
        for(int i =0;i<13  ;i++){
            Random random = new Random();
            int RI = random.nextInt(menus.size());
            Menu m =menus.get(RI);
            log.info(m.getName());
            menus.remove(RI);
            menuDeSemaine.add(m);

        }
        menuDeSemaineRepository.deleteAll();
        MenuDesemaine md = new MenuDesemaine();
        md.setMenuList(menuDeSemaine);
        menuDeSemaineRepository.save(md);





    }
   @Scheduled(fixedRate = 200000)
    public void prom(){

        List<MenuDesemaine>menuDesemaines = menuDeSemaineRepository.findAll();
        List<Menu>menus  = menuDesemaines.get(0).getMenuList();
      List<MenuPreferences>preferences =  menuPreferencesRepository.findAll();
      Menu menu = new Menu();
            menu = menus.get(6);
      for(int i =0;i<preferences.size();i++){
          String s= preferences.get(i).getMail();
          javaEmailService.sendEmail(s,"Promotion de 50 % !!!","Promotion de 50 % sur ce menu "+"  "+menu.getName());
          menu.setPrice(menu.getPrice()/2);
      }
      log.info("mail envoye");


    }





}