package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("nie udało sie stworzyc telefonu ", e);
        }

    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                CriteriaBuilder.In<String> in = cb.in(root.get(param.getKey()));
                for (String value : param.getValue()) {
                    in.value(value);
                }
                Predicate p = (param.getValue().length > 0) ? in : cb.conjunction();
                predicates.add(p);
            }
            cq.select(root).where(predicates.toArray(new Predicate[0]));
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones for parameters:" + params.toString());
        }
    }
}
