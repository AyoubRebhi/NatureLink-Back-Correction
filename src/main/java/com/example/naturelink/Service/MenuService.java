package Service;

import Entity.Menu;
import Repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }
}
